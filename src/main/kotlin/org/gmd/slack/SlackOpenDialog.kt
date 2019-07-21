package org.gmd.slack

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

class SlackOpenDialog() {

    @JsonProperty("trigger_id")
    lateinit var triggerId: String
    
    lateinit var dialog: SlackDialog

    constructor(triggerId: String, dialog: SlackDialog) : this() {
        this.triggerId = triggerId
        this.dialog = dialog
    }

    fun asJson(): String {
        return ObjectMapper().writeValueAsString(this)
    }
}

