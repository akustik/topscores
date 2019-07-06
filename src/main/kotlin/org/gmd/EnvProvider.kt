package org.gmd

import java.util.concurrent.locks.ReentrantLock

interface EnvProvider {
    fun getEnv(): Map<String, String>

    fun getCurrentTimeInMillis(): Long

    fun getLockFor(account: String, tournament: String): ReentrantLock
}