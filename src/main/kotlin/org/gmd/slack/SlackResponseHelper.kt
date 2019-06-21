package org.gmd.slack

import com.fasterxml.jackson.databind.ObjectMapper

class SlackResponseHelper {
    var slackResponse = SlackResponse()

    fun internalMessage(text: String) {
        slackResponse = SlackResponse(text = text)
    }

    fun publicMessage(text: String, attachements: List<String> = emptyList()) {
        slackResponse = SlackResponse(
                text = text,
                attachments = attachements.map { a -> SlackAttachment(a) },
                responseType = "in_channel"
        )
    }

    fun asJson(): String {
        return ObjectMapper().writeValueAsString(slackResponse)
    }

}