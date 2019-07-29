package org.gmd.slack.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.gmd.util.JsonUtils.Companion.writeValueAsString

class SlackOpenDialog() {

    @JsonProperty("trigger_id")
    lateinit var triggerId: String

    lateinit var dialog: SlackDialog

    constructor(triggerId: String, dialog: SlackDialog) : this() {
        this.triggerId = triggerId
        this.dialog = dialog
    }

    fun asJson(): String {
        return writeValueAsString(this)
    }
}

