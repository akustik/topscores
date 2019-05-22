package org.gmd

import org.gmd.model.Game
import org.gmd.model.Score
import org.gmd.service.AuthorizationService
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
    
    @Autowired
    lateinit private var auth: AuthorizationService

    @RequestMapping("/{account}/games/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addGame(@PathVariable("account") account: String, @RequestBody game: Game): Game {
        auth.withAutorization(account = account, providedToken = "none")
        return service.addGame(account, withCollectionTimeIfTimestampIsNotPresent(game))
    }

    @RequestMapping("/{account}/scores/{tournament}")
    @ResponseBody
    internal fun scores(@PathVariable("account") account: String,
                        @PathVariable("tournament") tournament: String): List<Score> {
        auth.withAutorization(account = account, providedToken = "none")
        return service.computeTournamentScores(account = account, tournament = tournament)
    }

    @RequestMapping("/{account}/games/list")
    internal fun listGames(@PathVariable("account") account: String, model: MutableMap<String, Any>): String {
        auth.withAutorization(account = account, providedToken = "none")
        val games = service.listGames(account)
        val output = games.map { t -> "Read from DB: " + t.tournament + ", " + t.timestamp + ", " + t.toJsonBytes().size }
        model.put("records", output)
        return "db"
    }

    private fun withCollectionTimeIfTimestampIsNotPresent(game: Game): Game {
        game.timestamp = game.timestamp?.let { game.timestamp } ?: System.currentTimeMillis()
        return game
    }
}