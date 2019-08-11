package org.gmd.slack.command

import com.github.ajalt.clikt.core.CliktCommand
import org.gmd.service.AsyncGameService
import org.gmd.slack.SlackResponseHelper

class Ping(
        val response: SlackResponseHelper, 
        val service: AsyncGameService, 
        val account: String, 
        val tournament: String) 
    : CliktCommand(help = "Ping the system"), SlackCommand {
    override fun run() {
        response.asyncMessage("Pong!")
        response.asyncDefaultResponse()
    }
}