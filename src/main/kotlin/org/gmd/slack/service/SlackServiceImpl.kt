package org.gmd.slack.service

import org.gmd.EnvProvider
import org.gmd.slack.executor.SlackExecutorProvider
import org.gmd.slack.model.SlackTeamAuth
import org.gmd.slack.repository.SlackRepository
import org.gmd.util.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

@Component
class SlackServiceImpl(private val env: EnvProvider,
                       private val slackExecutorProvider: SlackExecutorProvider,
                       private val slackRepository: SlackRepository,
                       private val jedisPool: JedisPool?) : SlackService {

    companion object {
        val oneWeekInSeconds = 7 * 24 * 3600
        val logger: Logger = LoggerFactory.getLogger(SlackServiceImpl::class.java)
    }

    override fun oauth(code: String): SlackTeamAuth {
        val slackSecret = env.getEnv()[EnvProvider.SLACK_CLIENT_SECRET]
        val slackClientId = env.getEnv()[EnvProvider.SLACK_CLIENT_ID]
        val response = slackExecutorProvider.oauthExecutor()(slackClientId!!, slackSecret!!, code)
        slackRepository.storeAuth(response)
        return response
    }

    override fun postWebApi(teamName: String, method: String, jsonBody: String, useBotToken: Boolean): String {
        val auth = slackRepository.getAuthByTeamName(teamName)
        val token = if (useBotToken) auth.bot.botAccessToken else auth.accessToken
        return slackExecutorProvider.webApiExecutor()(method, jsonBody, token)
    }

    override fun getWebApi(teamName: String, method: String): List<String> {
        val auth = slackRepository.getAuthByTeamName(teamName)
        return slackExecutorProvider.webApiPaginatedExecutor()(method, auth.accessToken)
    }

    @Async
    override fun registerChannelActivity(teamName: String, channelId: String, channelName: String) {
        jedisPool!!.resource.use {
            it.setex(channelIdRedisKey(teamName = teamName, channelName = channelName), oneWeekInSeconds, channelId)
        }
    }

    override fun getChannelIdByName(teamName: String, channelName: String): String? {
        jedisPool!!.resource.use {
            return it.get(channelIdRedisKey(teamName = teamName, channelName = channelName))
        }
    }

    private fun channelIdRedisKey(teamName: String, channelName: String) = "$teamName#$channelName"

    override fun getTeamName(teamId: String): String {
       return slackRepository.getAuthByTeamId(teamId).teamName
    }

    override fun getUserNameById(teamName: String, id: String): String? =
            getRedisUserByKey(teamName = teamName, redisKey = userIdRedisKey(teamName = teamName, id = id))

    override fun getUserIdByName(teamName: String, name: String): String? =
            getRedisUserByKey(teamName = teamName, redisKey = userNameRedisKey(teamName = teamName, name = name))

    private fun getRedisUserByKey(teamName: String, redisKey: String): String? {
        jedisPool!!.resource.use {
            val value = it!!.get(redisKey)
            return if (value == null) {
                recoverTeamUsers(it, teamName)
                it.get(redisKey)
            } else {
                value
            }
        }
    }

    private fun userIdRedisKey(teamName: String, id: String) = "$teamName#$id"

    private fun userNameRedisKey(teamName: String, name: String) = "$teamName#${name.toLowerCase()}"

    private fun recoverTeamUsers(jedis: Jedis, teamName: String) {
        logger.info("Going to recover all users for $teamName")
        val allUsers = getAllUsersById(teamName)
        val idToName = allUsers.flatMap { listOf(userIdRedisKey(teamName = teamName, id = it.key), it.value) }
        val nameToId = allUsers.flatMap { listOf(userNameRedisKey(teamName = teamName, name = it.value), it.key) }
        val allEntries = (idToName + nameToId).toTypedArray()
        jedis.mset(*allEntries)
        logger.info("Recovered and updated ${allUsers.size} users for $teamName")
    }

    private fun getAllUsersById(teamName: String): Map<String, String> {
        return getWebApi(teamName = teamName, method = "users.list")
                .flatMap { r -> JsonUtils.readTree(r)["members"].map { it["id"].asText() to it["name"].asText().toLowerCase()  } }
                .toMap()
    }

}