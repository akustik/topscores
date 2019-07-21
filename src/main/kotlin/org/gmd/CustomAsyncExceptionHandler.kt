package org.gmd

import org.gmd.util.JsonUtils.Companion.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method

open class CustomAsyncExceptionHandler : AsyncUncaughtExceptionHandler {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(CustomAsyncExceptionHandler::class.java)
    }

    override fun handleUncaughtException(
            throwable: Throwable, method: Method, vararg obj: Any) {
        val arguments = obj.map { a -> JSON.writeValueAsString(a) }
        logger.error("Unable to perform async execution with method ${method.name} and args $arguments", throwable)
    }

}
