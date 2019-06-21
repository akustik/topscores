package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import org.gmd.model.Game
import org.gmd.model.Party
import org.gmd.model.Team
import org.gmd.model.TeamMember
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class Add(val response: SlackResponseHelper, val service: GameService, val account: String, val tournament: String) : CliktCommand(help = "Add a new game", printHelpOnEmptyArgs = true) {
    val players by argument(help = "Ordered list of the scoring of the event, i.e: winner loser").multiple(required = true)
    override fun run() {
        val normalizedPlayers = players.map { p -> p.toLowerCase() }
        val parties = normalizedPlayers.reversed().mapIndexed { index, player ->
            Party(
                    team = Team(player),
                    members = listOf(TeamMember(player)),
                    score = index + 1,
                    metrics = emptyList(),
                    tags = emptyList()
            )
        }

        val gameToCreate = Game(
                tournament = tournament,
                parties = parties,
                timestamp = System.currentTimeMillis()
        )

        val storedGame = service.addGame(account, gameToCreate)
        val storedPlayers = storedGame.parties
                .sortedByDescending { party -> party.score }
                .flatMap { p -> p.members.map { m -> m.name } }

        val scores = storedPlayers.mapIndexed { index, s -> "${index + 1}. $s" }.joinToString(separator = "\n")

        response.publicMessage("Good game! A new game entry has been created!",
                listOf(scores))
    }
}