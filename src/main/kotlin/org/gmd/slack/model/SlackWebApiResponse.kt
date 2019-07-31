package org.gmd.slack.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.gmd.util.JsonUtils.Companion.readValueWithUnknownProperties

class SlackWebApiResponse() {

    var ok: Boolean = false
    var error: String? = null

    @JsonProperty("response_metadata")
    var responseMetadata: SlackWebApiResponseMetadata? = null

    constructor(ok: Boolean, error: String? = null, responseMetadata: SlackWebApiResponseMetadata? = null) : this() {
        this.ok = ok
        this.error = error
        this.responseMetadata = responseMetadata
    }

    companion object {
        fun fromJson(json: String): SlackWebApiResponse {
            return readValueWithUnknownProperties(json, SlackWebApiResponse::class.java)
        }
    }

    override fun toString(): String {
        return "SlackWebApiResponse(ok=$ok, error=$error, responseMetadata=$responseMetadata)"
    }
}

