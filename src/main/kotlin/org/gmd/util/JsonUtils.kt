package org.gmd.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

class JsonUtils {

    companion object {
        val JSON = ObjectMapper()
        val JSON_W_UNKNOWN = ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}