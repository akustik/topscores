package org.gmd

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.gmd.model.Game
import org.gmd.model.Score
import org.gmd.model.TournamentStatus
import org.gmd.service.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Api(value="Main API", description = "Game & rating operations")
@Controller
class Topscores {

    @Autowired
    lateinit private var service: GameService
    
    private val JSON = ObjectMapper()
    
    @RequestMapping("/", method = arrayOf(RequestMethod.GET))
    internal fun index(authentication: Authentication, model: MutableMap<String, Any>): String {
        val games = service.listGames(authentication.name)
        model.put("games", games)
        return "index"
    }

    @ApiOperation(value = "Stores a game into the system")
    @RequestMapping("/games/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addGame(authentication: Authentication, @RequestBody game: Game): Game {
        return service.addGame(authentication.name, withCollectionTimeIfTimestampIsNotPresent(game))
    }

    @ApiOperation(value = "List all the games for a given account")
    @RequestMapping("/games/list", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    internal fun listGames(authentication: Authentication): List<Game> {
        return service.listGames(authentication.name)
    }
    
    @ApiOperation(value = "Ranks the players of a given tournament")
    @RequestMapping("/scores/{tournament}/players", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    internal fun scores(authentication: Authentication,
                        @PathVariable("tournament") tournament: String,
                        @ApiParam(value = "Algorithm to be used in the ranking", required = false, allowableValues = "SUM, ELO")
                        @RequestParam(name = "alg", defaultValue = "SUM") algorithm: String): TournamentStatus {

        val scores = service.computeTournamentMemberScores(
                account = authentication.name, 
                tournament = tournament, 
                alg = Algorithm.valueOf(algorithm.toUpperCase())
        )
        
        val metrics = service.computeTournamentMemberMetrics(
                account = authentication.name,
                tournament = tournament
        )
        
        return TournamentStatus(scores, metrics)
    }
    
    private fun withCollectionTimeIfTimestampIsNotPresent(game: Game): Game {
        game.timestamp = game.timestamp?.let { game.timestamp } ?: System.currentTimeMillis()
        return game
    }
}