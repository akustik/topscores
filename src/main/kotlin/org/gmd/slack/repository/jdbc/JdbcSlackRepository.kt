package org.gmd.slack.repository.jdbc

import org.gmd.slack.repository.SlackRepository
import org.gmd.slack.model.SlackTeamAuth
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
        jdbcTemplate.update("INSERT INTO $tableName(team_name, content) VALUES (?,?)", 
                auth.teamName.toLowerCase(), auth.toJsonBytes())
    }

    override fun getAuth(teamName: String): SlackTeamAuth {
        val tableName = withTable()
        return jdbcTemplate.query(
                """ 
                    SELECT created_at, content from $tableName
                    where team_name = ? order by created_at desc limit 1
                    """, arrayOf(teamName.toLowerCase()), SlackAuthRowMapper()).first()!!.second
    }

    private fun withTable(): String {
        val tableName = "slack_auth"
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS $tableName " +
                "(team_name TEXT NOT NULL, created_at TIMESTAMP DEFAULT NOW(), content bytea NOT NULL, PRIMARY KEY (team_name, created_at))")
        return tableName
    }
}