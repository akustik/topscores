package org.gmd.service

import org.gmd.slack.SlackTeamAuth

interface SlackService {

    fun oauth(code: String): SlackTeamAuth

}