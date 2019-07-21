package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import org.gmd.service.SlackService
import org.gmd.slack.SlackDialog
import org.gmd.slack.SlackDialogSelectUserElement
import org.gmd.slack.SlackOpenDialog
import org.gmd.slack.SlackResponseHelper

class Dialog(
        val response: SlackResponseHelper,
        val service: SlackService,
        val triggerId: String?,
        val account: String,
        val tournament: String)
    : CliktCommand(help = "Open an interactive dialog"), SlackCommand {
    override fun run() {
        val players = (1..4).map { idx -> SlackDialogSelectUserElement("Player #$idx", "player_$idx") }
        val dialog = SlackDialog("something-random", "Game result", players)
        val openDialog = SlackOpenDialog(triggerId!!, dialog)
        service.postWebApi(account, "dialog.open", openDialog.asJson())
        response.asyncDefaultResponse()
    }
}