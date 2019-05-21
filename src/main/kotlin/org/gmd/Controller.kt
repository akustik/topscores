package org.gmd

import org.gmd.model.Game
import org.gmd.model.Score
import org.gmd.service.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class Controller {

    @RequestMapping("/")
    internal fun index(): String {
        return "index"
    }

    @RequestMapping("/hello")
    internal fun hello(model: MutableMap<String, Any>): String {
        val energy = System.getenv().get("SAMPLE")
        model.put("science", "is very hard, " + energy)
        return "hello"
    }

    @Autowired
    lateinit private var service: GameService

    @RequestMapping("/games/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addGame(@RequestBody game: Game): Game {
        game.timestamp = game.timestamp?.let { game.timestamp } ?: System.currentTimeMillis()
        return service.addGame(game)
    }

    @RequestMapping("/scores/{account}")
    @ResponseBody
    internal fun scores(@PathVariable("account") account: String): List<Score> {
        return service.getAccountScores(account)
    }

    @RequestMapping("/games/list")
    internal fun listGames(model: MutableMap<String, Any>): String {
        val games = service.listGames()
        val output = games.map { t -> "Read from DB: " + t.account + ", " + t.timestamp + ", " + t.toJsonBytes().size }
        model.put("records", output)
        return "db"
    }
}