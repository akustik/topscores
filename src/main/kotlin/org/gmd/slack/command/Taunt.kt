package org.gmd.slack.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import org.gmd.slack.SlackResponseHelper
import org.gmd.slack.model.SlackPostMessage
import org.gmd.slack.service.SlackService

class Taunt(
        val response: SlackResponseHelper,
        val service: SlackService,
        val account: String,
        val channelId: String)
    : CliktCommand(help = "Taunt a group of players through the slack bot"), SlackCommand {

    val players by argument(help = "List of players").multiple(required = true)

    override fun run() {
        val normalizedPlayers = normalizePlayers(players).flatten()

        val text = normalizedPlayers.joinToString(" and ", "Hey ", ", what about a game?!")
            { createUserId(service.getUserIdByName(teamName = account, name = it), it) }

        val message = SlackPostMessage(channelId = channelId, text = text).asJson()

        service.postWebApi(account, "chat.postMessage", message, useBotToken = true)
        response.emptyResponse()
    }

    private fun createUserId(id: String?, name: String): String {
        return if (id != null) {
            "<@$id>"
        } else {
            name
        }
    }
}
