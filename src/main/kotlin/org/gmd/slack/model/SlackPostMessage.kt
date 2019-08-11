package org.gmd.slack.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.gmd.util.JsonUtils.Companion.writeValueAsString

class SlackPostMessage() {

    @JsonProperty("channel")
    lateinit var channelId: String
    lateinit var text: String
    lateinit var attachments: List<SlackAttachment>

    constructor(channelId: String, text: String, attachments: List<SlackAttachment> = emptyList()) : this() {
        this.channelId = channelId
        this.text = text
        this.attachments = attachments
    }

    fun asJson(): String {
        return writeValueAsString(this)
    }
}

