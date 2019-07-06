package org.gmd.slack

import org.gmd.Topscores
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class DefaultSlackAsyncExecutorProvider : SlackAsyncExecutorProvider {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(DefaultSlackAsyncExecutorProvider::class.java)
    }

    override fun executorFor(responseUrl: String): (SlackResponse) -> Unit = { response ->
        run {
            val responseBody = response.asJson()
            val template = RestTemplate()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val request = HttpEntity<String>(responseBody, headers)
            val responseEntity = template.postForEntity(responseUrl, request, String::class.java)

            if (responseEntity.statusCode != HttpStatus.OK) {
                Topscores.logger.error("Unable to deliver async response {} with response {}", response.asJson(), responseEntity)
            }
        }
    }
}