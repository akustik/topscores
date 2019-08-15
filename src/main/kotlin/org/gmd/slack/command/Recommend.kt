package org.gmd.slack.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import org.gmd.Algorithm
import org.gmd.model.Game
import org.gmd.model.Team
import org.gmd.service.GameService
import org.gmd.service.alg.ELOMemberRatingAlgorithm.Companion.probabilityOfWinForBRating
import org.gmd.slack.SlackResponseHelper
import org.gmd.util.calculateWinRatioForA

class Recommend(
        val response: SlackResponseHelper,
        val service: GameService,
        val account: String,
        val tournament: String,
        username: String)
    : CliktCommand(help = "Print a list of recommended rivals"), SlackCommand {

    val player by argument(help = "Player name").default(username)
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()
    val min by option("--min", help = "min matches to consider").int().default(3)

    override fun run() {
        returnOrderedRecommendations()
        response.asyncDefaultResponse()
    }

    private fun returnOrderedRecommendations() {
        val games = service.listGames(account, tournament, 1000)
        val team = Team(player)
        val matchingGames = games.filter { game -> game.contains(team) }
        val allPlayersScores = service.computeTournamentMemberScores(account, tournament, Algorithm.ELO)
        val playerScore = allPlayersScores.find { x -> x.member == player }
                ?: return playerNotFound()
        val otherPlayersWithAtLeastMinDirectMatches = allPlayersScores
                .filter { x -> x.member != player }
                .filter { other -> directMatches(matchingGames, other.member).size >= min }
        val otherPlayersWithSortingValue = otherPlayersWithAtLeastMinDirectMatches
                .map { otherScore ->
                    run {
                        val playerWinProbability = calculateWinRatioForA(directMatches(matchingGames, otherScore.member), team, Team(otherScore.member))!!.second
                        val playerRatingWinProbability = probabilityOfWinForBRating(otherScore.score.toDouble(), playerScore.score.toDouble())
                        val probabilityDiff = Math.round((playerWinProbability - playerRatingWinProbability) * 100).toInt()
                        Pair(otherScore, probabilityDiff)
                    }
                }

        val sortedRecommendations = otherPlayersWithSortingValue
                .sortedByDescending { it.second }
                .mapIndexed { index, pair -> "${index + 1}. ${pair.first.member} (${pair.second})" }
                .joinToString(separator = "\n")

        response.asyncMessage(text = "Recommended rivals for $player (+100 the most recommended and -100 the least recommended)", attachments = listOf(sortedRecommendations), silent = silent)
    }

    private fun playerNotFound() {
        response.asyncMessage(text = "$player does not have any match", silent = silent)
    }

    private fun directMatches(matchingGames: List<Game>, challenger: String) =
            matchingGames.filter { game -> game.contains(Team(challenger)) }
}