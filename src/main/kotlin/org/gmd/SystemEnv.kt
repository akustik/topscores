package org.gmd

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

@Component
open class SystemEnv : EnvProvider {

    val locks = ConcurrentHashMap<String, ReentrantLock>()

    override fun getEnv(): Map<String, String> {
        return System.getenv()!!
    }

    override fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun getLockFor(account: String, tournament: String): ReentrantLock {
        return locks.getOrPut("$account#$tournament") { ReentrantLock() }
    }
}