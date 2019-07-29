package org.gmd.util

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.gmd.slack.service.SlackServiceImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException

class JsonUtils {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(JsonUtils::class.java)

        private val JSON = ObjectMapper()
        private val JSON_W_UNKNOWN = ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        fun <T> readValue(content: String, valueType: Class<T>): T {
            return withLogging(content, valueType) { c, t -> JSON.readValue(c, t)}
        }

        fun <T> readValue(content: ByteArray, valueType: Class<T>): T {
            return withLogging(content, valueType) { c, t -> JSON.readValue(c, t)}
        }

        fun writeValueAsBytes(value: Any): ByteArray {
            return JSON.writeValueAsBytes(value)
        }

        fun writeValueAsString(value: Any): String {
            return JSON.writeValueAsString(value)
        }

        fun readTree(content: String): JsonNode {
            return withLogging(content) {JSON.readTree(it)}
        }

        fun <T> readValueWithUnknownProperties(content: String, valueType: Class<T>): T {
            return withLogging(content, valueType) { c, t -> JSON_W_UNKNOWN.readValue(c, t)}
        }

        private fun <T> withLogging(content: T, parser: (T) -> JsonNode): JsonNode {
            try {
                return parser(content)
            } catch (e: JsonParseException) {
                logger.error("Unable to parse JsonNode with $content", e)
                throw e
            }
        }

        private fun <T,U> withLogging(content: U, valueType: Class<T>, parser: (U, Class<T>) -> T): T {
            try {
                return parser(content, valueType)
            } catch (e: JsonParseException) {
                logger.error("Unable to parse content $content with class ${valueType.name}", e)
                throw e
            }
        }
    }
}