package org.gmd.slack

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

class SlackTeamAuth {

    var ok: Boolean? = false
    @JsonProperty("access_token")
    var accessToken: String
    var scope: String
    @JsonProperty("team_id")
    var teamId: String

    constructor(ok: Boolean,
                accessToken: String,
                scope: String,
                teamId: String) {
        this.ok = ok
        this.accessToken = accessToken
        this.scope = scope
        this.teamId = teamId
    }
    
    fun toJsonBytes(): ByteArray {
        return ObjectMapper().writeValueAsBytes(this)
    }
}

