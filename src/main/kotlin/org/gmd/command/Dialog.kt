package org.gmd.command

import com.fasterxml.jackson.databind.JsonNode
import com.github.ajalt.clikt.core.CliktCommand
import org.gmd.slack.service.SlackService
import org.gmd.slack.model.SlackDialog
import org.gmd.slack.model.SlackDialogSelectUserElement
import org.gmd.slack.model.SlackOpenDialog
import org.gmd.slack.SlackResponseHelper

class Dialog(
        val response: SlackResponseHelper,
        val service: SlackService,
        val triggerId: String?,
        val account: String,
        val tournament: String)
    : CliktCommand(help = "Open an interactive dialog"), SlackCommand {

    companion object {
        const val SLACK_ADD_GAME_DIALOG: String = "slack-add-game-dialog"

        fun playerList(callbackId: String, submission: JsonNode): List<String> {
            if (callbackId == SLACK_ADD_GAME_DIALOG) {
                return (1..4).map { idx -> submission["player_$idx"].asText() }
            } else {
                throw IllegalArgumentException("Unknown submission with callback_id $callbackId")
            }
        }
    }

    override fun run() {
        val players = (1..4).map { idx -> SlackDialogSelectUserElement("Player #$idx", "player_$idx") }
        val dialog = SlackDialog(SLACK_ADD_GAME_DIALOG, "Game result", players)
        val openDialog = SlackOpenDialog(triggerId!!, dialog)
        service.postWebApi(account, "dialog.open", openDialog.asJson())
        response.asyncDefaultResponse()
    }
}