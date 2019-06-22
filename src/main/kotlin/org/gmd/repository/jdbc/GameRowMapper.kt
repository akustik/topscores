package org.gmd.repository.jdbc

import org.gmd.model.Game
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.Instant

class GameRowMapper : RowMapper<Pair<Instant, Game>> {
    override fun mapRow(rs: ResultSet?, rowNum: Int): Pair<Instant, Game> {
        val createdAt = rs!!.getTimestamp("created_at").toInstant()
        val content = rs.getBytes("content")

        return Pair(createdAt, Game.Companion.fromJsonBytes(content))
    }
}