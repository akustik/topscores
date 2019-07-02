package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.gmd.Algorithm
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class PrintEloAsync(val response: SlackResponseHelper, val service: GameService, val account: String, val tournament: String) : CliktCommand(help = "Print the current leaderboard") {
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()

    override fun run() {
        returnScores()
        response.internalMessage("The command is being computed")
    }

    fun returnScores() {
        service.consumeTournamentMemberScores(account, tournament, Algorithm.ELO, {
            scores ->
            run {
                if (scores.isNotEmpty()) {
                    val leaderboard = scores.mapIndexed { index, score -> "${index + 1}. ${score.member} (${score.score})" }
                            .joinToString(separator = "\n")

                    response.asyncMessage(text = "Current ELO leaderboard", attachments = listOf(leaderboard), silent = silent)
                } else {
                    response.asyncMessage(text = "There are no registered games yet. Add games to start the fun!", silent = silent)
                }
            }
        })
    }
}