package org.gmd.slack

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

class SlackResponse(val text: String = "Something went wrong!",
                    @get:JsonProperty("response_type")
                    val responseType: String = EPHEMERAL,
                    val attachments: List<SlackAttachment> = emptyList()) {

    companion object {
        val IN_CHANNEL = "in_channel"
        val EPHEMERAL = "ephemeral"
    }
}

