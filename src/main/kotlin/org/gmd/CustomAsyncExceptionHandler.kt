package org.gmd

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method


open class CustomAsyncExceptionHandler : AsyncUncaughtExceptionHandler {

    var logger = LoggerFactory.getLogger(CustomAsyncExceptionHandler::class.java)
    
    override fun handleUncaughtException(
            throwable: Throwable, method: Method, vararg obj: Any) {
        logger.error("Unable to perform async execution with method ${method.name} and args $obj", throwable)
    }

}
