package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.SQLException
import java.sql.Timestamp
import javax.sql.DataSource

@Component
class JdbcGameRepository: GameRepository {


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

    @Autowired
    lateinit private var jdbcTemplate: JdbcTemplate

    override fun listTicks(): List<Timestamp> {
        return jdbcTemplate.queryForList("select tick from ticks", Timestamp::class.java)
    }

}