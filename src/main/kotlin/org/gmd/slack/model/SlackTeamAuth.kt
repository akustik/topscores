package org.gmd.slack.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.gmd.util.JsonUtils.Companion.JSON

class SlackTeamAuth() {

    var ok: Boolean? = false
    @JsonProperty("access_token")
    lateinit var accessToken: String
    lateinit var scope: String
    @JsonProperty("team_id")
    lateinit var teamId: String
    @JsonProperty("team_name")
    lateinit var teamName: String
    @JsonProperty("user_id")
    lateinit var userId: String
    @JsonProperty("bot")
    lateinit var bot: SlackTeamBotAuth

    constructor(ok: Boolean,
                accessToken: String,
                scope: String,
                teamId: String,
                teamName: String,
                userId: String,
                bot: SlackTeamBotAuth) : this() {
        this.ok = ok
        this.accessToken = accessToken
        this.scope = scope
        this.teamId = teamId
        this.teamName = teamName
        this.userId = userId
        this.bot = bot
    }

    fun toJsonBytes(): ByteArray {
        return JSON.writeValueAsBytes(this)
    }

    override fun toString(): String {
        return "SlackTeamAuth(ok=$ok, accessToken='<HIDDEN>', scope='$scope', teamId='$teamId', teamName='$teamName', userId='$userId', bot=$bot)"
    }

    companion object {
        fun fromJsonBytes(bytes: ByteArray): SlackTeamAuth {
            return JSON.readValue(bytes, SlackTeamAuth::class.java)
        }
    }
}

