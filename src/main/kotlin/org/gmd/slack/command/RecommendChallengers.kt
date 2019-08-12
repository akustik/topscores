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
import org.gmd.model.Score
import org.gmd.model.Team
import org.gmd.service.GameService
import org.gmd.service.alg.eloRatingDeltaForAScore
import org.gmd.slack.SlackResponseHelper
import org.gmd.util.calculateWinRatio

class RecommendChallengers(
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
        returnEvolution()
        response.asyncDefaultResponse()
    }

    private fun returnEvolution() {
        val games = service.listGames(account, tournament, 1000)
        val team = Team(player)
        val matchingGames = games.filter { game -> game.contains(team) }
        val scores = service.computeTournamentMemberScores(account, tournament, Algorithm.ELO)
        val playerScore = scores.find { x -> x.member == player } ?: return notFoundPlayer(scores)
        val challengers = scores
                .asSequence()
                .filter { x -> x.member != player }
                .filter { challenger -> directMatches(matchingGames, challenger.member).size >= min }
                .sortedBy { challengerScore ->
                    calculateWinRatio(directMatches(matchingGames, challengerScore.member), Team(challengerScore.member), team)
                            .second
                            .map { challengerWinRatio -> sortValue(playerScore, challengerScore, challengerWinRatio) }
                            .orElse(100.toDouble())
                }
                .mapIndexed { index, score -> "${index + 1}. ${score.member}" }
                .joinToString(separator = "\n")
        response.asyncMessage(text = "Best challenges for $player", attachments = listOf(challengers), silent = silent)

    }

    private fun notFoundPlayer(scores: List<Score>) {
        val players = listOf(scores.joinToString(separator = "\n") { score -> score.member })
        response.asyncMessage(text = "Not found player $player in list", attachments = players, silent = silent)
    }

    private fun directMatches(matchingGames: List<Game>, challenger: String) =
            matchingGames.filter { game -> game.contains(Team(challenger)) }

    private fun sortValue(playerScore: Score, challengerScore: Score, winRatio: Int) =
            (winRatio + 0.01) * eloRatingDeltaForAScore(challengerScore.score.toDouble(), playerScore.score.toDouble())
}