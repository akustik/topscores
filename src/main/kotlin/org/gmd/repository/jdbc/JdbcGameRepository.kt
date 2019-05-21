package org.gmd.repository.jdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.gmd.model.Game
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
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
                ByteArray::class.java).map { bytes -> Game.Companion.fromJsonBytes(bytes) }
    }

    override fun listGames(account: String): List<Game> {
        val response: List<ByteArray> = jdbcTemplate.queryForList("select content from games where account = :account",
                ByteArray::class.java, MapSqlParameterSource("account", account))
        return response.map { bytes -> Game.Companion.fromJsonBytes(bytes) }
    }

    override fun addGame(game: Game): Game {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS games (account TEXT NOT NULL, created_at TIMESTAMP DEFAULT NOW(), content bytea NOT NULL)")
        jdbcTemplate.update("INSERT INTO games(account, content) VALUES (?,?)", game.account, game.toJsonBytes())
        return game
    }


}