package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.gmd.Algorithm
import org.gmd.model.Game
import org.gmd.model.Score
import org.gmd.model.Team
import org.gmd.service.GameService
import org.gmd.service.alg.eloRatingDeltaForAScore
import org.gmd.slack.SlackResponseHelper
import org.gmd.slack.command.SlackCommand
import org.gmd.util.calculateWinRatio
import java.lang.Math.abs

class RecommendChallengers(
        val response: SlackResponseHelper,
        val service: GameService,
        val account: String,
        val tournament: String,
        username: String)
    : CliktCommand(help = "Print a list of recommended rivals"), SlackCommand {

    val player by argument(help = "Player name").default(username)
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()

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
                .filter { player -> haveEverWon(matchingGames, player, team) }
                .sortedBy { player ->
                    calculateWinRatio(directMatches(matchingGames, player), team, Team(player.member))
                            .second
                            .map { z -> sortValue(playerScore, player, z) }
                            .orElse(100.toDouble())
                }
                .mapIndexed { index, score -> "${index + 1}. ${score.member}" }
                .joinToString(separator = "\n")
        response.asyncMessage(text = "Best challenges for $player", attachments = listOf(challengers), silent = silent)

    }

    private fun haveEverWon(matchingGames: List<Game>, player: Score, team: Team): Boolean {
        val matches = directMatches(matchingGames, player)
        return matches.isNotEmpty() && calculateWinRatio(matches, team, Team(player.member)).second.map { ratio -> ratio > 0 }.orElse(false)
    }

    private fun notFoundPlayer(scores: List<Score>) {
        val players = listOf(scores.joinToString(separator = "\n") { score -> score.member })
        response.asyncMessage(text = "Not found player $player in list", attachments = players, silent = silent)
    }

    private fun directMatches(matchingGames: List<Game>, x: Score) =
            matchingGames.filter { y -> y.contains(Team(x.member)) }

    private fun sortValue(playerScore: Score, challengerScore: Score, winRatio: Int) =
            abs((1  - winRatio) * eloRatingDeltaForAScore(challengerScore.score.toDouble(), playerScore.score.toDouble()))
}