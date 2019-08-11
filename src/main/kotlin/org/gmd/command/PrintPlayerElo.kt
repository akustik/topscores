package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import org.gmd.model.Evolution.Companion.computePlayerEvolution
import org.gmd.service.AsyncGameService
import org.gmd.slack.SlackResponseHelper

class PrintPlayerElo(
        val response: SlackResponseHelper,
        val service: AsyncGameService,
        val account: String,
        val tournament: String,
        username: String)
    : CliktCommand(help = "Print the current ELO evolution for a player"), SlackCommand {

    val player by argument(help = "Player name").default(username)
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()
    val alg by option("--alg", "-a", help = "The algorithm to compute the ranking").choice("elo", "sum").default("elo")

    override fun run() {
        returnEvolution()
        response.asyncDefaultResponse()
    }

    private fun returnEvolution() {
        val algorithm = parseAlgorithm(alg)
        service.consumeTournamentMemberScoreEvolution(account, tournament, listOf(player), algorithm, emptyList()) { evolution ->
            run {
                if (evolution.isNotEmpty()) {
                    val playerEvolution = computePlayerEvolution(evolution.first())
                    response.asyncMessage(text = "Current ${algorithm.name} evolution for $player", attachments = listOf(playerEvolution), silent = silent)
                } else {
                    response.asyncMessage(text = "There are no registered games for $player yet. Add games to start the fun!", silent = silent)
                }
            }
        }
    }
}