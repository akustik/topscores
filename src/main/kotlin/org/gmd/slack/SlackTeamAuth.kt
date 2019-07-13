package org.gmd.slack

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

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

    constructor(ok: Boolean,
                accessToken: String,
                scope: String,
                teamId: String,
                teamName: String, 
                userId: String): this() {
        this.ok = ok
        this.accessToken = accessToken
        this.scope = scope
        this.teamId = teamId
        this.teamName = teamName
        this.userId = userId
    }
    
    fun toJsonBytes(): ByteArray {
        return ObjectMapper().writeValueAsBytes(this)
    }

    override fun toString(): String {
        return "SlackTeamAuth(ok=$ok, accessToken='<HIDDEN>', scope='$scope', teamId='$teamId', teamName='$teamName', userId='$userId')"
    }


}

