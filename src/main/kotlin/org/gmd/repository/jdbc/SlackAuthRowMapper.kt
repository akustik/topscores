package org.gmd.repository.jdbc

import org.gmd.slack.SlackTeamAuth
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.Instant

class SlackAuthRowMapper : RowMapper<Pair<Instant, SlackTeamAuth>> {
    override fun mapRow(rs: ResultSet?, rowNum: Int): Pair<Instant, SlackTeamAuth> {
        val createdAt = rs!!.getTimestamp("created_at").toInstant()
        val content = rs.getBytes("content")

        return Pair(createdAt, SlackTeamAuth.fromJsonBytes(content))
    }
}