package org.gmd.repository.jdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.gmd.model.Game
import org.gmd.repository.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.SQLException
import javax.sql.DataSource

@Component
open class JdbcGameRepository : GameRepository {
    
    @Value("\${spring.datasource.url}")
    private var dbUrl: String? = null
    
    companion object {
        private val MODEL_VERSION = 1
        private val ACCOUNT_PATTERN = Regex("\\w+")
    }
    
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

    override fun listGames(account: String): List<Game> {
        val tableName = createTableName(account)
        return jdbcTemplate.queryForList("select content from $tableName",
                ByteArray::class.java).map { bytes -> Game.Companion.fromJsonBytes(bytes) }
    }

    override fun listGames(account: String, tournament: String): List<Game> {
        val tableName = createTableName(account)
        val response: List<ByteArray> = jdbcTemplate.queryForList("select content from $tableName where tournament = ?",
                arrayOf(account), ByteArray::class.java)
        return response.map { bytes -> Game.Companion.fromJsonBytes(bytes) }
    }

    override fun addGame(account: String, game: Game): Game {
        val tableName = createTableName(account)
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS $tableName (tournament TEXT NOT NULL, created_at TIMESTAMP DEFAULT NOW(), content bytea NOT NULL)")
        jdbcTemplate.update("INSERT INTO $tableName(tournament, content) VALUES (?,?)", game.tournament, game.toJsonBytes())
        return game
    }
    
    private fun createTableName(account: String): String {
        assert(ACCOUNT_PATTERN.matches(account), { -> "Account $account is not valid" })
        return "${account}_game_$MODEL_VERSION"
    }


}