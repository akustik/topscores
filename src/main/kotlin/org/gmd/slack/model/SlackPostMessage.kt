package org.gmd.slack.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.gmd.util.JsonUtils.Companion.writeValueAsString

class SlackPostMessage() {

    @JsonProperty("channel")
    lateinit var channelId: String
    lateinit var text: String

    constructor(channelId: String, text: String) : this() {
        this.channelId = channelId
        this.text = text
    }

    fun asJson(): String {
        return writeValueAsString(this)
    }
}

