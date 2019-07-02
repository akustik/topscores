package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.gmd.Algorithm
import org.gmd.service.AsyncGameService
import org.gmd.slack.SlackResponseHelper

class PrintPlayerElo(
        val response: SlackResponseHelper,
        val service: AsyncGameService,
        val account: String,
        val tournament: String,
        username: String)
    : CliktCommand(help = "Print the current ELO evolution for a player") {

    val player by argument(help = "Player name").default(username)
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()

    override fun run() {
        returnEvolution()
        response.asyncDefaultResponse();
    }

    fun returnEvolution() {
        service.consumeTournamentMemberScoreEvolution(account, tournament, listOf(player), Algorithm.ELO, emptyList(), {
            evolution ->
            run {
                if (evolution.isNotEmpty()) {
                    val leaderboard = evolution.first().score.mapIndexed { index, score -> "${index + 1}. $score" }
                            .joinToString(separator = "\n")

                    response.asyncMessage(text = "Current ELO evolution for $player", attachments = listOf(leaderboard), silent = silent)
                } else {
                    response.asyncMessage(text = "There are no registered games for $player yet. Add games to start the fun!", silent = silent)
                }
            }
        })
    }
}