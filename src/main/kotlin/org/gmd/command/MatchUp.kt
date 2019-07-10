package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import org.gmd.Algorithm
import org.gmd.model.Team
import org.gmd.model.TeamMatchUp
import org.gmd.service.AsyncGameService
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class MatchUp(
        val response: SlackResponseHelper,
        val service: GameService,
        val account: String,
        val tournament: String,
        username: String)
    : CliktCommand(help = "Print the current ELO evolution for a player") {

    val player1 by argument(help = "Player1 name")
    val player2 by argument(help = "Player2 name").default(username)
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()

    override fun run() {
        returnEvolution()
        response.asyncDefaultResponse()
    }

    private fun returnEvolution() {
        val games = service.listGames(account, 1000)
        val team1 = Team(player1)
        val team2 = Team(player2)
        val matchingGames = games.filter { game -> game.contains(team1) && game.contains(team1) }

        val total = matchingGames.size
        if(total > 0) {
            val winRatio = TeamMatchUp(team1, team2, matchingGames).calculateWinRatio()
            response.message(text ="From $total games $player1 beats $player2 a $winRatio%", silent = silent)
        } else {
            response.message(text = "No matches found", silent = silent)
        }
    }
}