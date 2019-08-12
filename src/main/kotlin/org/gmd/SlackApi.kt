package org.gmd

import com.github.ajalt.clikt.core.*
import com.google.common.hash.Hashing
import io.swagger.annotations.Api
import org.apache.commons.lang3.text.StrTokenizer
import org.gmd.command.RecommendChallengers
import org.gmd.model.Evolution
import org.gmd.service.AsyncGameService
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper
import org.gmd.slack.command.*
import org.gmd.slack.executor.SlackExecutorProvider
import org.gmd.slack.model.SlackAttachment
import org.gmd.slack.model.SlackPostMessage
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

@Api(value = "Slack API", description = "Slack interactions")
@Controller
class SlackApi(private val env: EnvProvider, private val slackExecutorProvider: SlackExecutorProvider) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SlackApi::class.java)
    }

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var slackService: SlackService

    @Autowired
    private lateinit var asyncService: AsyncGameService

    @RequestMapping("/trigger/slack/summary/{hours}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    internal fun triggerChannelSummary(
            authentication: Authentication,
            @PathVariable("hours") hours: Int): String {

        val account = authentication.name
        val existingTournaments = gameService.listTournaments(account)

        val executions = existingTournaments.map { tournament ->
            run {
                val channelId = slackService.getChannelIdByName(teamName = account, channelName = tournament)
                if (channelId != null) {
                    logger.info("Triggering slack summary action for $account and $tournament ($channelId)")

                    asyncService.consumeTournamentMemberScoreEvolution(
                            account = account,
                            tournament = tournament,
                            alg = Algorithm.ELO,
                            consumer = {
                                val evolution = Evolution.computeRatingChangesForTime(it,
                                        minTimestamp = env.getCurrentTimeInMillis() - hours * 3600 * 1000)
                                if (evolution.trim().isNotEmpty()) {
                                    val message = SlackPostMessage(
                                            channelId = channelId,
                                            text = "Hey! These are the ELO changes for the last $hours hours",
                                            attachments = listOf(SlackAttachment(evolution))
                                    ).asJson()
                                    slackService.postWebApi(account, "chat.postMessage", message, useBotToken = true)
                                } else {
                                    logger.warn("No trends for $tournament with $hours hours")
                                }
                            }
                    )

                    "#$tournament: OK"
                } else {
                    "#$tournament: MISS"
                }
            }
        }

        return executions.joinToString(separator = ", ")
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
        slackService.registerChannelActivity(teamName = teamDomain, channelId = channelId, channelName = channelName)

        val cmd = Leaderboard().subcommands(
                AddGame(responseHelper, env, gameService, asyncService, teamDomain, channelName),
                PrintElo(responseHelper, asyncService, teamDomain, channelName),
                Ping(responseHelper, asyncService, teamDomain, channelName),
                PrintPlayerElo(responseHelper, asyncService, teamDomain, channelName, userName),
                PrintGames(responseHelper, gameService, teamDomain, channelName),
                DeleteGame(responseHelper, gameService, teamDomain, channelName),
                MatchUp(responseHelper, gameService, teamDomain, channelName, userName),
                Dialog(responseHelper, slackService, triggerId, teamDomain, channelName),
                Taunt(responseHelper, slackService, teamDomain, channelId),
                RecommendChallengers(responseHelper, gameService, teamDomain, channelName, userName)
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


    @RequestMapping("/slack/event", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    internal fun slackEvent(@RequestBody body: String): String {

        val tree = readTree(body)
        return if (tree.get("challenge") != null) {
            tree.get("challenge").asText()
        } else {
            val channelId = tree["event"]["channel"].asText()
            val teamId = tree["team_id"].asText()
            val teamName = slackService.getTeamName(teamId)

            //Check slack signature
            logger.info("channel $channelId event received, team $teamName ($teamId), signature validity: false")

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
}