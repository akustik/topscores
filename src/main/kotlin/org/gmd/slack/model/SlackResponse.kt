package org.gmd.slack.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.gmd.util.JsonUtils.Companion.JSON

class SlackResponse(val text: String = "Something went wrong!",
                    @get:JsonProperty("response_type")
                    val responseType: String = EPHEMERAL,
                    val attachments: List<SlackAttachment> = emptyList()) {

    companion object {
        val IN_CHANNEL = "in_channel"
        val EPHEMERAL = "ephemeral"
    }

    fun asJson(): String {
        return JSON.writeValueAsString(this)
    }
}

