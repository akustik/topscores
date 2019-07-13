package org.gmd

import java.util.concurrent.locks.ReentrantLock

interface EnvProvider {
    
    companion object {
        const val SLACK_SECRET = "slack_secret"
        const val SLACK_CLIENT_ID = "slack_client_id"
    }
    
    fun getEnv(): Map<String, String>

    fun getCurrentTimeInMillis(): Long

    fun getLockFor(account: String, tournament: String): ReentrantLock
}