package org.gmd

import java.util.concurrent.locks.ReentrantLock

open class EnvProviderForTesting(private val env: Map<String, String>, private val time: Long): EnvProvider {

    override fun getEnv(): Map<String, String> {
        return env
    }

    override fun getCurrentTimeInMillis(): Long {
        return time
    }

    override fun getLockFor(account: String, tournament: String): ReentrantLock {
        return ReentrantLock()
    }

}