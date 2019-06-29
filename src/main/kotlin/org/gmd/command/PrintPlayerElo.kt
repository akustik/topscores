package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.gmd.Algorithm
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class PrintPlayerElo(
        val response: SlackResponseHelper,
        val service: GameService,
        val account: String,
        val tournament: String,
        username: String)
    : CliktCommand(help = "Print the current ELO evolution for a player") {

    val player by option("--player", "-p", help = "Player name").default(username)
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()

    override fun run() {
        val evolution = service.computeTournamentMemberScoreEvolution(account, tournament, listOf(player), Algorithm.ELO).first()
        
        if(evolution.score.isNotEmpty()) {
            val leaderboard = evolution.score.mapIndexed { index, score -> "${index + 1}. $score" }
                    .joinToString(separator = "\n")

            response.message(text ="Current ELO evolution for $player", attachments = listOf(leaderboard), silent = silent)
        } else {
            response.message(text = "There are no registered games for that player yet. Add games to start the fun!", silent = silent)
        }
    }
}