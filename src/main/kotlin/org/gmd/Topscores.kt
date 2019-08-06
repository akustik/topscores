package org.gmd

import com.github.ajalt.clikt.core.*
import com.google.common.hash.Hashing
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.apache.commons.lang3.text.StrTokenizer
import org.gmd.command.*
import org.gmd.form.SimpleGame
import org.gmd.model.Game
import org.gmd.model.PlayerStatus
import org.gmd.model.TournamentMetrics
import org.gmd.model.TournamentStatus
import org.gmd.service.AsyncGameService
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper
import org.gmd.slack.executor.SlackExecutorProvider
import org.gmd.slack.service.SlackService
import org.gmd.util.JsonUtils.Companion.readTree
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.codec.Hex
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.nio.charset.Charset
import java.time.Instant
import java.time.format.DateTimeFormatter

@Api(value = "Main API", description = "Game & rating operations")
@Controller
class Topscores(private val env: EnvProvider, private val slackExecutorProvider: SlackExecutorProvider) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Topscores::class.java)
    }

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var slackService: SlackService

    @Autowired
    private lateinit var asyncService: AsyncGameService

    @RequestMapping("/", method = arrayOf(RequestMethod.GET))
    internal fun index(authentication: Authentication, model: MutableMap<String, Any>): String {
        val account = authentication.name
        withCommonWeb(account, model)
        withLastGames(account, model)
        withTournamentList(account, model)
        return "index"
    }

    private fun withLastGames(account: String, model: MutableMap<String, Any>) {
        model["games"] = gameService.listGames(account).sortedByDescending { it.timestamp }.take(5)
    }

    private fun withTournamentList(account: String, model: MutableMap<String, Any>) {
        model["tournaments"] = gameService.listTournaments(account)
    }

    private fun withCommonWeb(account: String, model: MutableMap<String, Any>) {
        model["account"] = account
        model["slack_client_id"] = env.getEnv().getValue(EnvProvider.SLACK_CLIENT_ID)
    }

    @RequestMapping("/web/status/{tournament}/{alg}", method = arrayOf(RequestMethod.GET))
    internal fun tournament(authentication: Authentication,
                            @PathVariable("tournament") tournament: String,
                            @PathVariable("alg") alg: String,
                            model: MutableMap<String, Any>): String {
        val account = authentication.name
        withCommonWeb(account, model)
        withTournamentList(account, model)
        withSelectedTournament(tournament, model)
        withStatus(tournamentStatus(account, tournament, alg), model)
        return "tournament"
    }

    private fun withSelectedTournament(tournament: String, model: MutableMap<String, Any>) {
        model["tournament"] = tournament
    }

    private fun withStatus(status: Any, model: MutableMap<String, Any>) {
        model["status"] = status
    }

    @RequestMapping("/web/status/{tournament}/player/{player}/{alg}", method = arrayOf(RequestMethod.GET))
    internal fun tournament(authentication: Authentication,
                            @PathVariable("tournament") tournament: String,
                            @PathVariable("player") player: String,
                            @PathVariable("alg") alg: String,
                            model: MutableMap<String, Any>): String {
        val account = authentication.name
        withCommonWeb(account, model)
        withTournamentList(account, model)
        withStatus(playerStatus(account, tournament, player, alg), model)
        return "player"
    }

    @RequestMapping("/web/create", method = arrayOf(RequestMethod.GET))
    internal fun create(authentication: Authentication, model: MutableMap<String, Any>): String {
        val account = authentication.name
        withCommonWeb(account, model)
        withTournamentList(account, model)
        return account
    }

    @ApiOperation(value = "Stores a new game into the system")
    @RequestMapping("/games/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addGame(authentication: Authentication, @RequestBody game: Game): Game {
        return gameService.addGame(authentication.name, Game.withCollectionTimeIfTimestampIsNotPresent(env, game))
    }

    @ApiOperation(value = "Stores a new game into the system with simpler syntax")
    @RequestMapping("/games/simple/add", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun addSimpleGame(authentication: Authentication,
                               @RequestBody game: SimpleGame): Game {
        val createdGame = Game.simpleGame(game)
        return gameService.addGame(authentication.name, Game.withCollectionTimeIfTimestampIsNotPresent(env, createdGame))
    }

    @ApiOperation(value = "List all the games for a given account")
    @RequestMapping("/games/list", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    internal fun listGames(authentication: Authentication): List<Game> {
        return gameService.listGames(authentication.name)
    }

    @ApiOperation(value = "Upload a full tournament with the format idx;first;second;third;fourth")
    @RequestMapping("/games/upload/simple/{tournament}", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun uploadTournament(authentication: Authentication,
                                  @PathVariable("tournament") tournament: String,
                                  @RequestBody body: String): Int {
        val lines = body.split("\n")
        val games: List<Pair<Int, List<String>>> = lines.map { line ->
            run {
                val tokens = StrTokenizer(line, ';', '"').tokenList
                Pair(Integer.parseInt(tokens.first()!!), tokens.drop(1))
            }
        }

        var addedGames = 0
        games.forEach { game ->
            run {
                val gameToCreate = Game.playerOrderedListToGame(tournament, game.second)
                gameService.addGame(authentication.name, Game.withTimestamp(env.getCurrentTimeInMillis() + game.first, gameToCreate))
                addedGames += 1
            }
        }

        return addedGames
    }

    @RequestMapping("/health/check", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    internal fun healthCheck(): String {
        return "OK"
    }

    @RequestMapping("/slack/command", method = arrayOf(RequestMethod.POST), consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    @ResponseBody
    internal fun slackCommand(
            @RequestParam(name = "response_url") responseUrl: String,
            @RequestParam(name = "user_name") userName: String,
            @RequestParam(name = "text", defaultValue = "") text: String,
            @RequestParam(name = "team_domain") teamDomain: String,
            @RequestParam(name = "channel_id") channelId: String,
            @RequestParam(name = "channel_name") channelName: String,
            @RequestBody body: String,
            @RequestHeader(name = "X-Slack-Signature") slackSignature: String,
            @RequestHeader(name = "X-Slack-Request-Timestamp") slackTimestamp: String,
            @RequestParam(name = "trigger_id", required = false) triggerId: String?): String {


        return withSignatureValidation(
                teamDomain = teamDomain,
                slackSignature = slackSignature,
                slackTimestamp = slackTimestamp,
                body = body,
                responseUrl = responseUrl,
                block = { responseHelper ->
                    executeSlackCommand(teamDomain = teamDomain, channelId = channelId, channelName = channelName, userName = userName,
                            triggerId = triggerId, text = text, responseHelper = responseHelper)
                }).asJson() ?: ""
    }

    private fun withSignatureValidation(teamDomain: String, slackSignature: String, slackTimestamp: String,
                                        responseUrl: String, body: String, block: (SlackResponseHelper) -> Unit): SlackResponseHelper {

        val responseHelper = SlackResponseHelper(slackExecutorProvider.asyncResponseExecutorFor(responseUrl))

        if (env.getEnv()["token:$teamDomain"] == null) {
            return responseHelper
        }

        val bypassSecret = env.getEnv()[EnvProvider.BYPASS_SLACK_SIGNING_SECRET]?.equals("true")
                ?: false
        if (bypassSecret || isSlackSignatureValid(slackSignature, slackTimestamp, body)) {
            block(responseHelper)
        } else {
            responseHelper.internalMessage("Invalid signature. Please, review the application secret.")
        }

        return responseHelper
    }

    private fun isSlackSignatureValid(slackSignature: String, slackTimestamp: String, body: String): Boolean {
        val charset = Charset.defaultCharset()
        val slackSecret = env.getEnv()[EnvProvider.SLACK_SIGNING_SECRET]
        val baseString = "v0:$slackTimestamp:$body"
        val signature = Hashing.hmacSha256(slackSecret!!.toByteArray(charset)).hashString(baseString, charset)
        val coded = "v0=" + String(Hex.encode(signature.asBytes()))

        val isValid = slackSignature.equals(coded, ignoreCase = true)

        if (!isValid) {
            logger.error("Invalid signature for request with body $body")
        }

        return isValid
    }

    private fun executeSlackCommand(teamDomain: String, channelId: String, channelName: String, userName: String,
                                    triggerId: String?, text: String, responseHelper: SlackResponseHelper) {
        val cmd = Leaderboard().subcommands(
                AddGame(responseHelper, env, gameService, asyncService, teamDomain, channelName),
                PrintElo(responseHelper, asyncService, teamDomain, channelName),
                Ping(responseHelper, asyncService, teamDomain, channelName),
                PrintPlayerElo(responseHelper, asyncService, teamDomain, channelName, userName),
                PrintGames(responseHelper, gameService, teamDomain, channelName),
                DeleteGame(responseHelper, gameService, teamDomain, channelName),
                MatchUp(responseHelper, gameService, teamDomain, channelName, userName),
                Dialog(responseHelper, slackService, triggerId, teamDomain, channelName),
                Taunt(responseHelper, slackService, teamDomain, channelId)
        )

        try {

            val arguments = if (text.isNotEmpty()) {
                val cleansedText = text
                        .replace("\u201C", "\"") //fix quotes
                        .replace("\u201D", "\"") //fix quotes
                        .replace("@", "") //remove at
                StrTokenizer(cleansedText, ' ', '"').tokenList
            } else {
                emptyList()
            }

            cmd.parse(arguments)

        } catch (e: PrintHelpMessage) {
            responseHelper.internalMessage(e.command.getFormattedHelp())
        } catch (e: PrintMessage) {
            responseHelper.internalMessage(e.message!!)
        } catch (e: UsageError) {
            val message = "Error: " + e.message
            responseHelper.internalMessage(message)
        } catch (e: CliktError) {
            responseHelper.internalMessage(e.message!!)
        } catch (e: Abort) {
            responseHelper.internalMessage("Aborted!")
        }

    }

    @RequestMapping("/web/slack/oauth", method = arrayOf(RequestMethod.GET))
    internal fun slackAuth(
            @RequestParam(name = "code") code: String,
            authentication: Authentication,
            model: MutableMap<String, Any>): String {
        slackService.oauth(code)

        val account = authentication.name
        withLastGames(account, model)
        withTournamentList(account, model)
        withCommonWeb(account, model)
        return "index"
    }

    @RequestMapping("/slack/event", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun slackEvent(@RequestBody body: String): String {
        logger.info("event: $body")
        val tree = readTree(body)
        return if (tree.get("challenge") != null) {
            tree.get("challenge").asText()
        } else {
            //FIXME: Verify signature
            // Do use payload parameter?
            val channelId = tree["event"]["channel"].asText()
            val teamId = tree["team_id"].asText()

            // Register channel recent activity
            // Find how to map a channel id to a channel name, using a command?
            // Use this information to provide daily insights with an external call

            "ok"
        }
    }

    @RequestMapping("/slack/interactive", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun slackInteractive(@RequestParam payload: String,
                                  @RequestBody body: String,
                                  @RequestHeader(name = "X-Slack-Signature") slackSignature: String,
                                  @RequestHeader(name = "X-Slack-Request-Timestamp") slackTimestamp: String) {

        val decodedPayload = URLDecoder.decode(payload, "UTF-8")
        val parsedPayload = readTree(decodedPayload)

        val teamDomain = parsedPayload["team"]["domain"].asText()
        val channelId = parsedPayload["channel"]["id"].asText()
        val channelName = parsedPayload["channel"]["name"].asText()
        val userName = parsedPayload["user"]["name"].asText()
        val callbackId = parsedPayload["callback_id"].asText()
        val submission = parsedPayload["submission"]
        val responseUrl = parsedPayload["response_url"].asText()
        
        val players = Dialog.playerList(callbackId = callbackId, submission = submission)
                .joinToString(separator = " ")
                { slackService.getUserNameById(teamName = teamDomain, id = it) ?: "undefined" }

        val text = "addgame $players"

        logger.info("command to run: $text")

        withSignatureValidation(
                teamDomain = teamDomain,
                slackSignature = slackSignature,
                slackTimestamp = slackTimestamp,
                body = body,
                responseUrl = responseUrl,
                block = { responseHelper ->
                    executeSlackCommand(teamDomain = teamDomain, channelId = channelId, channelName = channelName, userName = userName,
                            triggerId = null, text = text, responseHelper = responseHelper)
                }).currentResponseAsyncMessage()
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
        val scores = gameService.computeTournamentMemberScores(
                account = account,
                tournament = tournament,
                alg = Algorithm.valueOf(algorithm.toUpperCase())
        )

        val metrics = gameService.computeTournamentMemberMetrics(
                account = account,
                tournament = tournament
        )

        val tournamentMetrics = metrics.map { m ->
            TournamentMetrics(
                    m.member,
                    m.metrics.groupBy({ metric -> metric.name }, { metric -> metric.value }).mapValues { entry -> entry.value.sum() })
        }
        val availableMetrics = metrics.flatMap { m -> m.metrics.map { n -> n.name } }.distinct().sorted()

        return TournamentStatus(scores, tournamentMetrics, availableMetrics)
    }

    private fun playerStatus(account: String, tournament: String, player: String, algorithm: String): PlayerStatus {
        val evolution = gameService.computeTournamentMemberScoreEvolution(
                account = account,
                tournament = tournament,
                player = listOf(player),
                alg = Algorithm.valueOf(algorithm.toUpperCase())
        )

        val metrics = gameService.computeTournamentMemberMetrics(
                account = account,
                tournament = tournament
        ).filter { metric -> metric.member.equals(player) }

        return PlayerStatus(evolution.first(), metrics)
    }

    @ApiOperation(value = "List all entries for a given account and tournament")
    @RequestMapping("/entries/{tournament}/list", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    internal fun listEntries(authentication: Authentication,
                             @PathVariable("tournament") tournament: String): List<String> {
        return gameService.listEntries(account = authentication.name, tournament = tournament)
                .map { e -> DateTimeFormatter.ISO_INSTANT.format(e.first) }
    }

    @ApiOperation(value = "Deletes an entry for a given account and tournament")
    @RequestMapping("/entries/{tournament}/delete", method = arrayOf(RequestMethod.DELETE))
    @ResponseBody
    internal fun deleteEntry(authentication: Authentication,
                             @PathVariable("tournament") tournament: String,
                             @RequestBody createdAt: String): Boolean {
        val instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(createdAt))
        return gameService.deleteEntry(account = authentication.name, tournament = tournament, createdAt = instant)
    }
}