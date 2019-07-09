package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import org.gmd.Algorithm
import org.gmd.service.AsyncGameService
import org.gmd.slack.SlackResponseHelper

class PrintElo(val response: SlackResponseHelper, val service: AsyncGameService, val account: String, val tournament: String) : CliktCommand(help = "Print the current leaderboard") {
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()
    val alg by option("--alg", "-a", help = "The algorithm to compute the ranking").choice("elo", "sum").default("elo")
    val players by argument(help = "Do only consider these players for ranking").multiple(required = false)

    override fun run() {
        returnScores()
        response.asyncDefaultResponse()
    }

    private fun returnScores() {
        val normalizedPlayers = players.map { p -> p.toLowerCase() }
        val algorithm = Algorithm.valueOf(alg.toUpperCase())
        service.consumeTournamentMemberScores(
                account = account,
                tournament = tournament,
                alg = algorithm,
                teams = normalizedPlayers) {
            scores ->
            run {
                if (scores.isNotEmpty()) {
                    val leaderboard = scores.mapIndexed { index, score -> "${index + 1}. ${score.member} (${score.score}, ${score.games})" }
                            .joinToString(separator = "\n")

                    response.asyncMessage(text = "Current ${algorithm.name} leaderboard", attachments = listOf(leaderboard), silent = silent)
                } else {
                    response.asyncMessage(text = "There are no registered games yet. Add games to start the fun!", silent = silent)
                }
            }
        }
    }
}