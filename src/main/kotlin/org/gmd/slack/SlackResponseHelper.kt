package org.gmd.slack

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate


class SlackResponseHelper(val responseUrl: String? = null) {

    companion object {
        var logger = LoggerFactory.getLogger(SlackResponseHelper::class.java)
    }

    var slackResponse = SlackResponse()

    fun internalMessage(text: String) {
        message(text = text, silent = true)
    }

    fun publicMessage(text: String, attachments: List<String> = emptyList()) {
        message(text = text, attachments = attachments, silent = false)
    }

    fun message(text: String, attachments: List<String> = emptyList(), silent: Boolean = true) {
        slackResponse = responseOf(text, attachments, silent)
    }
    
    fun asJson(): String {
        return slackResponse.asJson()
    }

    fun asyncMessage(text: String, attachments: List<String> = emptyList(), silent: Boolean = true) {
        val response = responseOf(text, attachments, silent)
        val body = ObjectMapper().writeValueAsString(response)
        val template = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity<String>(body, headers)
        val responseEntity = template.postForEntity(responseUrl!!, request, String::class.java)

        if (responseEntity.statusCode != HttpStatus.OK) {
            logger.error("Unable to deliver async response {} with response {}", response.asJson(), responseEntity)
        }
    }

    private fun responseOf(text: String, attachments: List<String>, silent: Boolean): SlackResponse {
        return SlackResponse(
                text = text,
                attachments = attachments.map { a -> SlackAttachment(a) },
                responseType = if (!silent) SlackResponse.IN_CHANNEL else SlackResponse.EPHEMERAL
        )
    }

}