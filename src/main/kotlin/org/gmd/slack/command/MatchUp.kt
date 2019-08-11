package org.gmd.slack.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.gmd.model.Team
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper
import org.gmd.util.calculateWinRatio

class MatchUp(
        val response: SlackResponseHelper,
        val service: GameService,
        val account: String,
        val tournament: String,
        username: String)
    : CliktCommand(help = "Print win rate between two players"), SlackCommand {

    val player1 by argument(help = "Player1 name")
    val player2 by argument(help = "Player2 name").default(username)
    val silent by option("--silent", "-s", help = "Do not show the slack response to everyone").flag()

    override fun run() {
        returnEvolution()
        response.asyncDefaultResponse()
    }

    private fun returnEvolution() {
        val games = service.listGames(account, tournament, 1000)
        val team1 = Team(player1)
        val team2 = Team(player2)

        val pair = calculateWinRatio(games, team1, team2)
        val total = pair.first
        val winRatio = pair.second

        if(winRatio.isPresent){
            response.asyncMessage(text = "From $total games $player1 beats $player2 a ${winRatio.get()}%.", silent = silent)
        } else {
            response.asyncMessage(text = "No matches found", silent = silent)
        }
    }

}
