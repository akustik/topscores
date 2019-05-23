package org.gmd.repository.jdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.sql.SQLException
import javax.sql.DataSource

@Component
open class ParametrizedDataSource {

    @Value("\${spring.datasource.url}")
    private var dbUrl: String? = null

    @Bean
    @Throws(SQLException::class)
    fun dataSource(): DataSource {
        if (dbUrl?.isEmpty() ?: true) {
            return HikariDataSource()
        } else {
            val config = HikariConfig()
            config.jdbcUrl = dbUrl
            return HikariDataSource(config)
        }
    }
}