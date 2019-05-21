package org.gmd

import org.gmd.model.Game
import org.gmd.model.Score
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

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

    @RequestMapping("/scores/{account}")
    @ResponseBody
    internal fun scores(@PathVariable("account") account: String): List<Score> {
        val scores: List<Pair<String, Int>> = repository.listGames(account)
                .flatMap { game -> game.parties }
                .flatMap {
                    party ->
                    party.members.map {
                        member ->
                        member.name to party.score
                    }
                }
        val sortedScores: SortedSet<Score> = scores.groupBy({ it.first }, { it.second })
                .mapValues { (k, v) -> v.sum() }
                .map { (k, v) -> Score(k, v) }
                .toSortedSet(compareBy({ score -> score.score }, { score -> score.member }))
        
        return sortedScores.reversed()
    }

    @RequestMapping("/games/list")
    internal fun listGames(model: MutableMap<String, Any>): String {
        val games = repository.listGames()
        val output = games.map { t -> "Read from DB: " + t.account + ", " + t.timestamp + ", " + t.toJsonBytes().size }
        model.put("records", output)
        return "db"
    }
}