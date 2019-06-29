package org.gmd.slack

import com.fasterxml.jackson.databind.ObjectMapper

class SlackResponseHelper {
    var slackResponse = SlackResponse()

    fun internalMessage(text: String) {
        message(text = text, silent = true)
    }

    fun publicMessage(text: String, attachments: List<String> = emptyList()) {
        message(text = text, attachments = attachments, silent = false)
    }

    fun message(text: String, attachments: List<String> = emptyList(), silent: Boolean = true) {
        slackResponse = SlackResponse(
                text = text,
                attachments = attachments.map { a -> SlackAttachment(a) },
                responseType = if(!silent) SlackResponse.IN_CHANNEL else SlackResponse.EPHEMERAL
        )
    }

    fun asJson(): String {
        return ObjectMapper().writeValueAsString(slackResponse)
    }

}