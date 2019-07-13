package org.gmd

import java.util.concurrent.locks.ReentrantLock

interface EnvProvider {
    
    companion object {
        const val SLACK_SIGNING_SECRET = "slack_signing_secret"
        const val SLACK_CLIENT_SECRET = "slack_client_secret"
        const val SLACK_CLIENT_ID = "slack_client_id"
    }
    
    fun getEnv(): Map<String, String>

    fun getCurrentTimeInMillis(): Long

    fun getLockFor(account: String, tournament: String): ReentrantLock
}