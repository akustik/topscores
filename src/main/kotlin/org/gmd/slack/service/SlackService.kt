package org.gmd.slack.service

import org.gmd.slack.model.SlackTeamAuth

interface SlackService {

    fun oauth(code: String): SlackTeamAuth

    fun postWebApi(teamName: String, method: String, jsonBody: String, useBotToken: Boolean = false): String
    
    fun getWebApi(teamName: String, method: String): List<String>
    
    fun getUserNameById(teamName: String, id: String): String?
    
    fun getUserIdByName(teamName: String, name: String): String?

    fun getTeamName(teamId: String): String

    fun registerChannelActivity(teamName: String, channelId: String, channelName: String)

    fun getChannelIdByName(teamName: String, channelName: String): String?

}