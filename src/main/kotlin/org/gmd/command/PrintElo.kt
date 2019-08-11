package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import org.gmd.model.Score
import org.gmd.service.AsyncGameService
import org.gmd.slack.SlackResponseHelper

class PrintElo(
        val response: SlackResponseHelper, 
        val service: AsyncGameService, 
        val account: String, 
        val tournament: String) 
    : CliktCommand(help = "Print the current leaderboard"), SlackCommand {
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()
    val alg by option("--alg", "-a", help = "The algorithm to compute the ranking").choice("elo", "sum").default("elo")
    val players by argument(help = "Do only consider these players for ranking").multiple(required = false)
    val minGames by option("--min-games", "-m", help="The minimum amount of games played to appear in the ranking").int().default(5)

    override fun run() {
        returnScores()
        response.asyncDefaultResponse()
    }

    private fun returnScores() {
        val normalizedPlayers = normalizePlayers(players)
        val algorithm = parseAlgorithm(alg)
        service.consumeTournamentMemberScores(
                account = account,
                tournament = tournament,
                alg = algorithm,
                teams = normalizedPlayers) {
            scores ->
            run {
                if (scores.isNotEmpty()) {
                    val leaderboard = Score.computeLeaderboard(scores, minGames)
                    val kind = if(players.isEmpty()) "complete" else "filtered"
                    response.asyncMessage(text = "Current ${algorithm.name} leaderboard ($kind) for players with at least $minGames games", attachments = listOf(leaderboard), silent = silent)
                } else {
                    response.asyncMessage(text = "There are no registered games yet. Add games to start the fun!", silent = silent)
                }
            }
        }
    }
}