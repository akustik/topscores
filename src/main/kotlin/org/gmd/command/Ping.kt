package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.gmd.Algorithm
import org.gmd.service.AsyncGameService
import org.gmd.slack.SlackResponseHelper

class Ping(val response: SlackResponseHelper, val service: AsyncGameService, val account: String, val tournament: String) : CliktCommand(help = "Ping the system") {
    override fun run() {
        response.asyncMessage("Pong!")
        response.asyncDefaultResponse()
    }
}