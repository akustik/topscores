package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.gmd.Algorithm
import org.gmd.EnvProvider
import org.gmd.model.*
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class AddGame(val response: SlackResponseHelper, val envProvider: EnvProvider, val service: GameService, val account: String, val tournament: String) : CliktCommand(help = "Add a new game", printHelpOnEmptyArgs = true) {
    val players by argument(help = "Ordered list of the scoring of the event, i.e: winner loser").multiple(required = true)
    val dryRun by option(help = "Returns an ELO simulation without actually storing the game").flag()
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()
    val withElo by option("--with-elo", help = "Show also the ELO updates after adding a new game").flag()

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
                timestamp = envProvider.getCurrentTimeInMillis()
        )

        if(dryRun) {
            val simulation = service.computeTournamentMemberScoreEvolution(
                    account = account,
                    tournament = tournament,
                    player = normalizedPlayers,
                    alg = Algorithm.ELO,
                    withGames = listOf(gameToCreate))


            response.message(
                    text = "Find the ELO simulation below, is it worth the risk?",
                    attachments = listOf(computeRatingChanges(simulation)),
                    silent = silent
            )

        } else {
            val storedGame = service.addGame(account, gameToCreate)
            response.publicMessage(
                    "Good game! A new game entry has been created!",
                    computeFeedbackAfterGameAdd(storedGame, normalizedPlayers)
            )
        }
    }

    private fun computeFeedbackAfterGameAdd(storedGame: Game, normalizedPlayers: List<String>): List<String> {
        val scores = computePlayerOrder(storedGame)
        return if(withElo) {
            val evolution = service.computeTournamentMemberScoreEvolution(account = account,
                    tournament = tournament,
                    player = normalizedPlayers,
                    alg = Algorithm.ELO)
            listOf(scores, computeRatingChanges(evolution))
        } else {
            listOf(scores)
        }
    }

    private fun variationToString(value: Int): String {
        return if(value > 0) "+$value" else "$value"
    }

    private fun computeRatingChanges(evolution: List<Evolution>): String {
        val eloUpdate = evolution
                .map { e -> Triple(e.member, e.score.last(), e.score.last() - e.score.dropLast(1).last()) }
                .sortedByDescending { p -> p.third }

        return eloUpdate.mapIndexed { index, s -> "${index + 1}. ${s.first} (${s.second}, ${variationToString(s.third)})" }.joinToString(separator = "\n")
    }

    private fun computePlayerOrder(game: Game): String {
        val storedPlayers = game.parties
                .sortedByDescending { party -> party.score }
                .flatMap { p -> p.members.map { m -> m.name } }

        return storedPlayers.mapIndexed { index, s -> "${index + 1}. $s" }.joinToString(separator = "\n")
    }
}