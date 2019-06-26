package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import org.gmd.Algorithm
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class PrintPlayerElo(val response: SlackResponseHelper, val service: GameService, val account: String, val tournament: String) : CliktCommand(help = "Print the current ELO evolution for a player", printHelpOnEmptyArgs = true) {
    val player by argument(help = "Player name")
    
    override fun run() {
        
        val evolution = service.computeTournamentMemberScoreEvolution(account, tournament, player, Algorithm.ELO)
        
        if(evolution.score.isNotEmpty()) {
            val leaderboard = evolution.score.mapIndexed { index, score -> "${index + 1}.$score" }
                    .joinToString(separator = "\n")

            response.publicMessage("Current ELO evolution for $player", listOf(leaderboard))            
        } else {
            response.publicMessage("There are no registered games for that player yet. Add games to start the fun!")   
        }
    }
}