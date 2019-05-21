package com.example

import com.example.model.Game
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class Controller {
    
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
    
    @Autowired
    lateinit private var repository: GameRepository

    @RequestMapping("/games/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addGame(@RequestBody game: Game): Game {
        game.timestamp = game.timestamp?.let { game.timestamp } ?: System.currentTimeMillis()
        return repository.addGame(game)
    }

    @RequestMapping("/games/list")
    internal fun listGames(model: MutableMap<String, Any>): String {
        val games = repository.listGames()
        val output = games.map { t -> "Read from DB: " + t.account + ", " + t.timestamp + ", " + t.toJsonBytes().size}
        model.put("records", output)
        return "db"
    }
}