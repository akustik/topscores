package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.deprecated
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import org.gmd.Algorithm
import org.gmd.EnvProvider
import org.gmd.model.*
import org.gmd.service.AsyncGameService
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper
import kotlin.concurrent.withLock

class AddGame(val response: SlackResponseHelper, val envProvider: EnvProvider, val service: GameService, val asyncService: AsyncGameService, val account: String, val tournament: String) : CliktCommand(help = "Add a new game", printHelpOnEmptyArgs = true) {
    val players by argument(help = "Ordered list of the scoring of the event, i.e: winner loser").multiple(required = true)
    val dryRun by option(help = "Returns an updated ranking simulation without actually storing the game").flag()
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()
    val force by option("--force", "-f", help = "Force the addition of the game and ignore collisions").flag()
    val withElo by option("--with-elo", help = "Show also the ranking updates after adding a new game").flag().deprecated("All additions already compute ranking")
    val alg by option("--alg", "-a", help = "The algorithm to compute the ranking").choice("elo", "sum").default("elo")

    companion object {
        fun playerOrderedListToGame(tournament: String, timestamp: Long, players: List<String>): Game {
            val parties = players.reversed().mapIndexed { index, player ->
                Party(
                        team = Team(player),
                        members = listOf(TeamMember(player)),
                        score = index + 1,
                        metrics = emptyList(),
                        tags = emptyList()
                )
            }

            return Game(
                    tournament = tournament,
                    parties = parties,
                    timestamp = timestamp
            )
        }
    }

    override fun run() {
        val normalizedPlayers = players.map { p -> p.toLowerCase() }
        val algorithm = Algorithm.valueOf(alg.toUpperCase())
        val gameToCreate = playerOrderedListToGame(tournament, envProvider.getCurrentTimeInMillis(), normalizedPlayers)

        if (dryRun) {
            response.asyncDefaultResponse()
            asyncService.consumeTournamentMemberScoreEvolution(
                    account = account,
                    tournament = tournament,
                    player = normalizedPlayers,
                    alg = algorithm,
                    withGames = listOf(gameToCreate),
                    consumer = { simulation ->
                        run {
                            response.asyncMessage("Find the ELO simulation below, is it worth the risk?",
                                    listOf(computeRatingChanges(simulation)),
                                    silent = silent)
                        }
                    }
            )
        } else {
            envProvider.getLockFor(account = account, tournament = tournament).withLock {
                if (!force && gameIsDuplicated(gameToCreate)) {
                    response.message("Please, wait some time before adding more games! " +
                            "Check last game for duplicates and use the --force flag if you're sure that is OK",
                            listOf(), silent = true)
                } else {
                    val storedGame = service.addGame(account, gameToCreate)
                    response.publicMessage(
                            "Good game! A new game entry has been created!",
                            computeFeedbackAfterGameAdd(storedGame, normalizedPlayers, algorithm)
                    )
                }
            }
        }
    }

    private fun gameIsDuplicated(gameToAdd: Game): Boolean {
        val lastEntry = service.listEntries(account, tournament, maxElements = 1)
        if (lastEntry.isNotEmpty()) {
            val entry = lastEntry.first()
            return computePlayerOrder(entry.second) == computePlayerOrder(gameToAdd)
        }

        return false
    }

    private fun computeFeedbackAfterGameAdd(storedGame: Game, normalizedPlayers: List<String>, algorithm: Algorithm): List<String> {
        asyncService.consumeTournamentMemberScoreEvolution(
                account = account,
                tournament = tournament,
                player = normalizedPlayers,
                alg = algorithm,
                withGames = emptyList(),
                consumer = { evolution ->
                    run {
                        response.asyncMessage("Computed ELO changes after this game",
                                listOf(computeRatingChanges(evolution)),
                                silent = false)
                    }
                }
        )
        return listOf(computePlayerOrder(storedGame))
    }

    private fun variationToString(value: Int): String {
        return if (value > 0) "+$value" else "$value"
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