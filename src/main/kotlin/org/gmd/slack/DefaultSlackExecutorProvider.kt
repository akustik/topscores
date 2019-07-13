package org.gmd.slack

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class DefaultSlackExecutorProvider : SlackExecutorProvider {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(DefaultSlackExecutorProvider::class.java)
    }

    override fun asyncResponseExecutorFor(responseUrl: String): (SlackResponse) -> Unit = { response ->
        run {
            val responseBody = response.asJson()
            val template = RestTemplate()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val request = HttpEntity(responseBody, headers)
            val responseEntity = template.postForEntity(responseUrl, request, String::class.java)

            if (responseEntity.statusCode != HttpStatus.OK) {
                logger.error("Unable to deliver async response {} with response {}", response.asJson(), responseEntity)
            }
        }
    }

    override fun oauthExecutor(oauthUrl: String): (clientId: String, clientSecret: String, code: String) -> SlackTeamAuth = { clientId: String, clientSecret: String, code: String ->
        run {
            val builder = UriComponentsBuilder
                    .fromHttpUrl(oauthUrl)
                    .queryParam("code", code)
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)

            val template = RestTemplate()
            val entity = template.getForEntity(builder.toUriString(), String::class.java)
            logger.info("Obtained oauth response as ${entity.body}")
            ObjectMapper().readValue(entity.body, SlackTeamAuth::class.java)
        }
    }
}