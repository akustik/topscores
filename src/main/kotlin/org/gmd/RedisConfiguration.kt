package org.gmd

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.net.URI


@Configuration
open class LocalRedisConfig {

    @Value("\${redis.url}")
    private var redisUrl: String? = null
    
    @Bean
    open fun jedisPool(): JedisPool? {
        if(!redisUrl.isNullOrEmpty()) {
            val redisURI = URI(redisUrl)
            val poolConfig = JedisPoolConfig()
            poolConfig.maxTotal = 10
            poolConfig.maxIdle = 5
            poolConfig.minIdle = 1
            poolConfig.testOnBorrow = true
            poolConfig.testOnReturn = true
            poolConfig.testWhileIdle = true
            return JedisPool(poolConfig, redisURI)
        }
        
        return null
    }

}