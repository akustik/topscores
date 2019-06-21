package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import org.gmd.Algorithm
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class Print(val response: SlackResponseHelper, val service: GameService, val account: String, val tournament: String) : CliktCommand(help = "Print the current leaderboard") {
    override fun run() {
        val scores = service.computeTournamentMemberScores(account, tournament, Algorithm.ELO)
        
        if(scores.isNotEmpty()) {
            val leaderboard = scores.mapIndexed { index, score -> "${index + 1}. ${score.member} (${score.score})" }
                    .joinToString(separator = "\n")

            response.publicMessage("Current ELO leaderboard", listOf(leaderboard))            
        } else {
            response.publicMessage("There are no registered games yet. Add games to start the fun!")   
        }
    }
}