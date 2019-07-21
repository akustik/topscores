package org.gmd.service

import org.gmd.slack.SlackTeamAuth

class SlackServiceForTesting: SlackService {
    override fun oauth(code: String): SlackTeamAuth {
        TODO("not implemented") 
    }

    override fun postWebApi(teamName: String, method: String, jsonBody: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}