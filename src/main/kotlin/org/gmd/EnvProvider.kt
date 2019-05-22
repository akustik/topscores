package org.gmd

interface EnvProvider {
    fun getEnv(): Map<String, String>
}