package org.gmd.service

import org.gmd.slack.SlackTeamAuth

interface SlackService {

    fun oauth(code: String): SlackTeamAuth

    fun postWebApi(teamName: String, method: String, jsonBody: String): String
}