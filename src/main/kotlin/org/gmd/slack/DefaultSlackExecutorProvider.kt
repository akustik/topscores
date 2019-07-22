package org.gmd.slack

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder


@Component
class DefaultSlackExecutorProvider : SlackExecutorProvider {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DefaultSlackExecutorProvider::class.java)
    }

    override fun asyncResponseExecutorFor(responseUrl: String): (SlackResponse) -> Unit = { response ->
        run {
            val responseBody = response.asJson()
            val template = RestTemplate()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON_UTF8

            val request = HttpEntity(responseBody, headers)
            verified(template.postForEntity(responseUrl, request, String::class.java))
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
            template.getForObject(builder.toUriString(), SlackTeamAuth::class.java)
        }
    }

    override fun webApiExecutor(url: String): (method: String, jsonBody: String, accessToken: String) -> String = { method: String, jsonBody: String, accessToken: String ->
        run {

            val template = RestTemplate()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON_UTF8

            headers["Authorization"] = "Bearer $accessToken"

            val request = HttpEntity(jsonBody, headers)
            val response = verified(template.postForEntity("$url/$method", request, String::class.java))

            response.first
        }
    }

    override fun webApiPaginatedExecutor(url: String): (method: String, accessToken: String) -> List<String> = { method: String, accessToken: String ->
        run {
            doPaginateWebApi(url, method, accessToken, null, emptyList())
        }
    }

    private tailrec fun doPaginateWebApi(url: String, method: String, accessToken: String, cursor: String?, acc: List<String>): List<String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val template = RestTemplate()
        val entity = HttpEntity<String>(headers)

        var builder = UriComponentsBuilder
                .fromHttpUrl("$url/$method")
                .queryParam("token", accessToken)


        if (!cursor.isNullOrEmpty()) {
            builder = builder.queryParam("cursor", cursor!!)
        }

        val response = verified(template.exchange(builder.toUriString(), HttpMethod.GET, entity, String::class.java))

        val webApiResponse = response.second

        return if (webApiResponse.responseMetadata == null || webApiResponse.responseMetadata!!.nextCursor.isNullOrEmpty()) {
            acc + response.first
        } else {
            doPaginateWebApi(
                    url, method,
                    accessToken,
                    webApiResponse.responseMetadata!!.nextCursor!!,
                    acc + response.first)
        }

    }

    private fun verified(responseEntity: ResponseEntity<String>): Pair<String, SlackWebApiResponse> {
        if (responseEntity.statusCode != HttpStatus.OK) {
            logger.error("Unable to execute slack request with response $responseEntity")
            throw IllegalStateException("Unable to execute slack request with response ${responseEntity.statusCode}")
        }

        val webApiResponse = SlackWebApiResponse.fromJson(responseEntity.body)

        if (!webApiResponse.ok) {
            logger.error("The method slack request failed with response $responseEntity")
            throw IllegalStateException("The method slack request failed with response $webApiResponse")
        }

        return Pair(responseEntity.body, webApiResponse)
    }
}