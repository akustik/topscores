package org.gmd

open class EnvProviderForTesting(private val env: Map<String, String>): EnvProvider {
    
    override fun getEnv(): Map<String, String> {
        return env
    }
}