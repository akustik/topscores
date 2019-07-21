package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.gmd.Algorithm
import org.gmd.service.AsyncGameService
import org.gmd.slack.SlackResponseHelper

class Dialog(
        val response: SlackResponseHelper,
        val triggerId: String,
        val account: String, 
        val tournament: String) 
    : CliktCommand(help = "Open an interactive dialog"), SlackCommand {
    override fun run() {
        response.asyncDefaultResponse()
    }
}