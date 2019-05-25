package org.gmd.repository.jdbc

import org.gmd.model.Game
import org.gmd.repository.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
open class JdbcGameRepository : GameRepository {

    companion object {
        private val MODEL_VERSION = 1
        private val ACCOUNT_PATTERN = Regex("\\w+")
    }

    @Autowired
    lateinit private var jdbcTemplate: JdbcTemplate

    override fun listGames(account: String): List<Game> {
        val tableName = withTableForAccount(account)
        return jdbcTemplate.queryForList("select content from $tableName",
                ByteArray::class.java).map { bytes -> Game.Companion.fromJsonBytes(bytes) }
    }

    override fun listGames(account: String, tournament: String): List<Game> {
        val tableName = withTableForAccount(account)
        val response: List<ByteArray> = jdbcTemplate.queryForList("select content from $tableName where tournament = ?",
                arrayOf(tournament), ByteArray::class.java)
        return response.map { bytes -> Game.Companion.fromJsonBytes(bytes) }
    }

    override fun addGame(account: String, game: Game): Game {
        val tableName = withTableForAccount(account)
        jdbcTemplate.update("INSERT INTO $tableName(tournament, content) VALUES (?,?)", game.tournament, game.toJsonBytes())
        return game
    }

    override fun listTournaments(account: String): List<String> {
        val tableName = withTableForAccount(account)
        return jdbcTemplate.queryForList("select distinct tournament from $tableName", String::class.java)
    }

    private fun withTableForAccount(account: String): String {
        assert(ACCOUNT_PATTERN.matches(account), { -> "Account $account is not valid" })
        val tableName = "${account}_game_$MODEL_VERSION"
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS $tableName (tournament TEXT NOT NULL, created_at TIMESTAMP DEFAULT NOW(), content bytea NOT NULL)")
        return tableName
    }
}