package org.gmd

import org.springframework.stereotype.Component

@Component
open class SystemEnv : EnvProvider {

    override fun getEnv(): Map<String, String> {
        return System.getenv()!!
    }
}