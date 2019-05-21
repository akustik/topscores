package com.example.jdbc

import com.example.GameRepository
import com.example.model.Game
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.SQLException
import javax.sql.DataSource

@Component
class JdbcGameRepository : GameRepository {

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

    override fun listGames(): List<Game> {
        return jdbcTemplate.queryForList("select content from games", 
                ByteArray::class.java).map { bytes -> Game.fromJsonBytes(bytes) }
    }

    override fun addGame(game: Game): Game {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS games (account TEXT NOT NULL, created_at TIMESTAMP DEFAULT NOW(), content bytea NOT NULL)")
        jdbcTemplate.update("INSERT INTO games(account, content) VALUES (?,?)", game.account, game.toJsonBytes())
        return game
    }


}