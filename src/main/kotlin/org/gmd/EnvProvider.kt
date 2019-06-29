package org.gmd

interface EnvProvider {
    fun getEnv(): Map<String, String>

    fun getCurrentTimeInMillis(): Long
}