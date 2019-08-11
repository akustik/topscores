package org.gmd.slack.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper

class DeleteGame(
        val response: SlackResponseHelper, 
        val service: GameService, 
        val account: String, 
        val tournament: String) 
    : CliktCommand(help = "Delete a game", printHelpOnEmptyArgs = true), SlackCommand {
    val index: Int by option(help = "Index of the game").int().required()
    override fun run() {
        val entries = service.listEntries(account, tournament, 10)
        if (entries.isNotEmpty()) {
            service.deleteEntry(account, tournament, entries.get(index).first)
            response.publicMessage("Deleted #$index!")
        } else {
            response.publicMessage("There is nothing to delete!")
        }
    }
}
