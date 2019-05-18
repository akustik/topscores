package com.example

import com.example.model.Game
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*
import javax.sql.DataSource

@Controller
class Controller {

    @Value("\${spring.datasource.url}")
    private var dbUrl: String? = null

    @Autowired
    lateinit private var dataSource: DataSource
    
    @Autowired
    lateinit private var jdbcTemplate : JdbcTemplate

    @RequestMapping("/")
    internal fun index(): String {
        return "index"
    }

    @RequestMapping("/hello")
    internal fun hello(model: MutableMap<String, Any>): String {
        val energy = System.getenv().get("SAMPLE");
        model.put("science", "is very hard, " + energy)
        return "hello"
    }

    @RequestMapping("/db")
    internal fun db(model: MutableMap<String, Any>): String {
        val connection = dataSource.getConnection()
        try {
            val stmt = connection.createStatement()
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
            stmt.executeUpdate("INSERT INTO ticks VALUES (now())")
            val rs = stmt.executeQuery("SELECT tick FROM ticks")

            val output = ArrayList<String>()
            while (rs.next()) {
                output.add("Read from DB: " + rs.getTimestamp("tick"))
            }

            model.put("records", output)
            return "db"
        } catch (e: Exception) {
            connection.close()
            model.put("message", e.message ?: "Unknown error")
            return "error"
        }

    }

    @RequestMapping("/games/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addGame(@RequestBody game: Game): Game {
        game.timestamp = game.timestamp?.let { game.timestamp } ?: System.currentTimeMillis()
        return game
    }

    @RequestMapping("/games/list")
    internal fun listGames(model: MutableMap<String, Any>): String {
        val ticks = jdbcTemplate.queryForList("select tick from ticks", Timestamp::class.java)
        val output = ticks.map { t -> "Read from DB: " + t }
        model.put("records", output)
        print(model)
        return "db"
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
}