package org.gmd.repository.jdbc

import org.gmd.model.Game
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.Timestamp

class GameRowMapper : RowMapper<Pair<Timestamp, Game>> {
    override fun mapRow(rs: ResultSet?, rowNum: Int): Pair<Timestamp, Game> {
        val createdAt = rs!!.getTimestamp("created_at")
        val content = rs.getBytes("content")

        return Pair(createdAt, Game.Companion.fromJsonBytes(content))
    }
}