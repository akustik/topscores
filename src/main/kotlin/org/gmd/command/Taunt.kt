package org.gmd.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import org.gmd.service.SlackService
import org.gmd.slack.SlackPostMessage
import org.gmd.slack.SlackResponseHelper
import org.gmd.util.JsonUtils

class Taunt(
        val response: SlackResponseHelper,
        val service: SlackService,
        val account: String,
        val channelId: String)
    : CliktCommand(help = "Taunt a group of players through the slack bot"), SlackCommand {

    val players by argument(help = "List of players").multiple(required = true)

    override fun run() {
        val normalizedPlayers = normalizePlayers(players)

        val allUsers = service.getWebApi(teamName = account, method = "users.list")
                .flatMap { r -> JsonUtils.JSON.readTree(r)["members"].map { it["name"].asText().toLowerCase() to it["id"].asText() } }
                .toMap()

        val text = normalizedPlayers
                .joinToString(" and ", "Hey ", ", what about a game?!")
                { p -> createUserId(allUsers, p) }

        val message = SlackPostMessage(channelId = channelId, text = text).asJson()

        service.postWebApi(account, "chat.postMessage", message, useBotToken = true)
        response.emptyResponse()
    }

    private fun createUserId(allUsers: Map<String, String>, name: String): String {
        return if (allUsers.containsKey(name)) {
            "<@${allUsers[name]}>"
        } else {
            name
        }
    }
}