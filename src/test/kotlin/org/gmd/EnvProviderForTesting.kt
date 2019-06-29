package org.gmd

open class EnvProviderForTesting(private val env: Map<String, String>, private val time: Long): EnvProvider {
    override fun getEnv(): Map<String, String> {
        return env
    }

    override fun getCurrentTimeInMillis(): Long {
        return time
    }

}