package org.gmd.repository.jdbc

import org.gmd.repository.SlackRepository
import org.gmd.slack.SlackTeamAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
open class JdbcSlackRepository : SlackRepository {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    override fun storeAuth(auth: SlackTeamAuth) {
        assert(auth.ok!!) { "Authentication failed" }
        val tableName = withTable()
        jdbcTemplate.update("INSERT INTO $tableName(team_id, content) VALUES (?,?)", auth.teamId, auth.toJsonBytes())
    }

    override fun getAuth(teamId: String): SlackTeamAuth {
        TODO("not implemented")
    }

    private fun withTable(): String {
        val tableName = "slack_auth"
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS $tableName " +
                "(team_id TEXT NOT NULL, created_at TIMESTAMP DEFAULT NOW(), content bytea NOT NULL, PRIMARY KEY (team_id, created_at))")
        return tableName
    }
}