package org.gmd

import org.gmd.model.Game
import org.gmd.model.Score
import org.gmd.service.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class Controller {

    @Autowired
    lateinit private var service: GameService
    
    @RequestMapping("/", method = arrayOf(RequestMethod.GET))
    internal fun index(): String {
        return "index"
    }

    @RequestMapping("/hello", method = arrayOf(RequestMethod.GET))
    internal fun hello(model: MutableMap<String, Any>): String {
        val energy = System.getenv().get("SAMPLE")
        model.put("science", "is very hard, " + energy)
        return "hello"
    }
    
    @RequestMapping("/games/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addGame(authentication: Authentication, @RequestBody game: Game): Game {
        return service.addGame(authentication.name, withCollectionTimeIfTimestampIsNotPresent(game))
    }

    @RequestMapping("/scores/{tournament}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    internal fun scores(authentication: Authentication,
                        @PathVariable("tournament") tournament: String,
                        @RequestParam(name = "alg", defaultValue = "SUM") algorithm: String): List<Score> {

        return service.computeTournamentScores(
                account = authentication.name, 
                tournament = tournament, 
                alg = Algorithm.valueOf(algorithm.toUpperCase())
        )
    }

    @RequestMapping("/games/list", method = arrayOf(RequestMethod.GET))
    internal fun listGames(authentication: Authentication, model: MutableMap<String, Any>): String {
        val games = service.listGames(authentication.name)
        val output = games.map { t -> "Read from DB: " + t.tournament + ", " + t.timestamp + ", " + t.toJsonBytes().size }
        model.put("records", output)
        return "db"
    }

    private fun withCollectionTimeIfTimestampIsNotPresent(game: Game): Game {
        game.timestamp = game.timestamp?.let { game.timestamp } ?: System.currentTimeMillis()
        return game
    }
}