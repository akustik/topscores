package org.gmd.slack.model

import com.fasterxml.jackson.annotation.JsonProperty

class SlackTeamBotAuth() {

    @JsonProperty("bot_user_id")
    lateinit var botUserId: String

    @JsonProperty("bot_access_token")
    lateinit var botAccessToken: String
    
    constructor(botUserId: String, botAccessToken: String) : this() {
        this.botUserId = botUserId
        this.botAccessToken = botAccessToken
    }

    override fun toString(): String {
        return "SlackTeamBotAuth(botUserId='$botUserId', botAccessToken='<HIDDEN>')"
    }


}

