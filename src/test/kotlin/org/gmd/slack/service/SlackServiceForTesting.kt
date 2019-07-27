package org.gmd.slack.service

import org.gmd.slack.model.SlackTeamAuth
import org.gmd.slack.service.SlackService

class SlackServiceForTesting : SlackService {
    override fun oauth(code: String): SlackTeamAuth {
        TODO("not implemented")
    }

    override fun postWebApi(teamName: String, method: String, jsonBody: String, useBotToken: Boolean): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWebApi(teamName: String, method: String): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}