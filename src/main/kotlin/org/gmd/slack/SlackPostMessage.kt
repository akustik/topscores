package org.gmd.slack

import com.fasterxml.jackson.annotation.JsonProperty
import org.gmd.util.JsonUtils.Companion.JSON

class SlackPostMessage() {

    @JsonProperty("channel")
    lateinit var channelId: String
    lateinit var text: String

    constructor(channelId: String, text: String) : this() {
        this.channelId = channelId
        this.text = text
    }

    fun asJson(): String {
        return JSON.writeValueAsString(this)
    }
}

