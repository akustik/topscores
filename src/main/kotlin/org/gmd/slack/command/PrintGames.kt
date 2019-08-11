package org.gmd.slack.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class PrintGames(
        val response: SlackResponseHelper,
        val service: GameService, 
        val account: String, 
        val tournament: String) 
    : CliktCommand(help = "Print the recent games"), SlackCommand {
    val maxElements: Int by option(help = "Max number of games").int().default(3)
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()

    override fun run() {
        val entries = service.listEntries(account, tournament, maxElements)

        if (entries.isNotEmpty()) {
            val games = entries.map { e -> e.second }

            val gamePlayers: List<String> = games.map { game ->
                run {
                    val storedPlayers = game.parties
                            .sortedByDescending { party -> party.score }
                            .flatMap { p -> p.members.map { m -> m.name } }

                    storedPlayers.mapIndexed { playerIdx, s -> "${playerIdx + 1}.$s" }.joinToString(separator = " ")
                }
            }

            val content = gamePlayers.mapIndexed { index, game -> "[$index] $game" }
                    .joinToString(separator = "\n")

            response.message(text ="Last games", attachments = listOf(content), silent = silent)
        } else {
            response.message(text = "There are no registered games. Add games to start the fun!", silent = silent)
        }
    }
}
