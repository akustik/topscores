package org.gmd

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.gmd.form.SimpleGame
import org.gmd.model.*
import org.gmd.service.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Api(value = "Main API", description = "Game & rating operations")
@Controller
class Topscores {

    @Autowired
    lateinit private var service: GameService

    @RequestMapping("/", method = arrayOf(RequestMethod.GET))
    internal fun index(authentication: Authentication, model: MutableMap<String, Any>): String {
        val account = authentication.name
        val games = service.listGames(account).sortedByDescending { it.timestamp }.take(5)
        val tournaments = service.listTournaments(account)
        model.put("games", games)
        model.put("account", account)
        model.put("tournaments", tournaments)
        return "index"
    }

    @RequestMapping("/web/status/{tournament}/{alg}", method = arrayOf(RequestMethod.GET))
    internal fun tournament(authentication: Authentication,
                            @PathVariable("tournament") tournament: String,
                            @PathVariable("alg") alg: String,
                            model: MutableMap<String, Any>): String {
        val account = authentication.name
        val status = tournamentStatus(account, tournament, alg)
        val tournaments = service.listTournaments(account)
        model.put("status", status)
        model.put("account", account)
        model.put("tournaments", tournaments)
        model.put("tournament", tournament)
        return "tournament"
    }

    @RequestMapping("/web/status/{tournament}/player/{player}/{alg}", method = arrayOf(RequestMethod.GET))
    internal fun tournament(authentication: Authentication,
                            @PathVariable("tournament") tournament: String,
                            @PathVariable("player") player: String,
                            @PathVariable("alg") alg: String,
                            model: MutableMap<String, Any>): String {
        val account = authentication.name
        val status = playerStatus(account, tournament, player, alg)
        val tournaments = service.listTournaments(account)
        model.put("status", status)
        model.put("account", account)
        model.put("tournaments", tournaments)
        return "player"
    }

    @RequestMapping("/web/create", method = arrayOf(RequestMethod.GET))
    internal fun create(authentication: Authentication, model: MutableMap<String, Any>): String {
        val account = authentication.name
        val tournaments = service.listTournaments(account)
        model.put("account", account)
        model.put("tournaments", tournaments)
        return account
    }

    @ApiOperation(value = "Stores a new game into the system")
    @RequestMapping("/games/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addGame(authentication: Authentication, @RequestBody game: Game): Game {
        return service.addGame(authentication.name, withCollectionTimeIfTimestampIsNotPresent(game))
    }

    @ApiOperation(value = "Stores a new game into the system with simpler syntax")
    @RequestMapping("/games/simple/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addSimpleGame(authentication: Authentication,
                               @RequestBody game: SimpleGame): Game {
        val parties = game.teams.map { t ->
            Party(
                    team = Team(name = t.team),
                    members = t.players.map { player -> TeamMember(name = player) },
                    score = t.score,
                    metrics = t.metrics.flatMap { metric -> metric.players.map { player -> Metric("${metric.metric}:${player}", metric.value) } },
                    tags = emptyList()
            )
        }
        val createdGame = Game(
                tournament = game.tournament,
                parties = parties,
                timestamp = System.currentTimeMillis()
        )
        return addGame(authentication, createdGame)
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
        return tournamentStatus(authentication.name, tournament, algorithm)
    }

    @ApiOperation(value = "Shows the evolution for a player of a given tournament")
    @RequestMapping("/scores/{tournament}/player/{player}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    internal fun evolution(authentication: Authentication,
                        @PathVariable("tournament") tournament: String,
                        @PathVariable("player") player: String,
                        @ApiParam(value = "Algorithm to be used in the ranking", required = false, allowableValues = "SUM, ELO")
                        @RequestParam(name = "alg", defaultValue = "SUM") algorithm: String): PlayerStatus {
        return playerStatus(authentication.name, tournament, player, algorithm)
    }

    private fun tournamentStatus(account: String, tournament: String, algorithm: String): TournamentStatus {
        val scores = service.computeTournamentMemberScores(
                account = account,
                tournament = tournament,
                alg = Algorithm.valueOf(algorithm.toUpperCase())
        )

        val metrics = service.computeTournamentMemberMetrics(
                account = account,
                tournament = tournament
        )
        
        val tournamentMetrics = metrics.map {
            m -> TournamentMetrics(
                m.member,
                m.metrics.groupBy({metric -> metric.name}, {metric -> metric.value}).mapValues { entry -> entry.value.sum() })
        }
        val availableMetrics = metrics.flatMap { m -> m.metrics.map { m -> m.name } }.distinct().sorted()

        return TournamentStatus(scores, tournamentMetrics, availableMetrics)
    }
    
    private fun playerStatus(account: String, tournament: String, player: String, algorithm: String): PlayerStatus {
        val scores = service.computeTournamentMemberScoreEvolution(
                account = account,
                tournament = tournament,
                player = player,
                alg = Algorithm.valueOf(algorithm.toUpperCase())
        )

        val metrics = service.computeTournamentMemberMetrics(
                account = account,
                tournament = tournament
        ).filter { metric -> metric.member.equals(player) }

        return PlayerStatus(scores, metrics)
    }

    private fun withCollectionTimeIfTimestampIsNotPresent(game: Game): Game {
        game.timestamp = game.timestamp?.let { game.timestamp } ?: System.currentTimeMillis()
        return game
    }
}