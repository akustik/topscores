package org.gmd

import com.fasterxml.jackson.databind.ObjectMapper
import org.gmd.model.Game
import org.gmd.repository.GameRepository
import org.gmd.repository.GameRepositoryForTesting
import org.gmd.service.*
import org.gmd.service.alg.AdderMemberRatingAlgorithm
import org.gmd.service.alg.ELOMemberRatingAlgorithm
import org.gmd.slack.SlackExecutorProviderForTesting
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.charset.Charset
import java.time.Instant
import java.util.*


@WebMvcTest(Topscores::class)
@RunWith(SpringRunner::class)
class TopscoresTest {

    companion object {
        val slackAsyncExecutorProviderForTesting = SlackExecutorProviderForTesting()
    }

    @Autowired
    private val mockMvc: MockMvc? = null

    @Configuration
    open class AppConfig {

        //FIXME: This needed due to not being able to use @MockBean directly to get mocks
        //of the required beens in this test. There are several solutions that need to be
        //tested, one of them updating to the latest version of Mockito that does not have
        //this problem and it's able to mock any class despite being final or private.

        /*
            val repo = mock(GameRepository::class.java)
            `when`(repo!!.addGame(any()))
                    .then({ invocation -> invocation.getArgumentAt(0, Game::class.java) })
            return repo
         */

        private val repository = GameRepositoryForTesting(listOf(Pair(Instant.now(), TestData.patxanga())))

        @Bean
        open fun authentication(): BasicConfiguration {
            return BasicConfiguration(EnvProviderForTesting(mapOf("token:user" to "pwd"), 1234L))
        }

        @Bean
        open fun gameRepository(): GameRepository {
            return repository
        }

        @Bean
        open fun gameService(): GameService {
            return GameServiceImpl(repository, AdderMemberRatingAlgorithm(), ELOMemberRatingAlgorithm())
        }
        
        @Bean
        open fun slackService(): SlackService {
            return SlackServiceForTesting()
        }

        @Bean
        open fun asyncGameService(): AsyncGameService {
            return AsyncGameServiceForTesting(GameServiceImpl(repository, AdderMemberRatingAlgorithm(), ELOMemberRatingAlgorithm()))
        }

        @Bean
        open fun controller(): Topscores {
            return Topscores(EnvProviderForTesting(mapOf(
                    "bypass_slack_secret" to "true",
                    "token:scopely" to "something",
                    "token:patxanga" to "something"
            ), 1234L), slackAsyncExecutorProviderForTesting)
        }
    }

    @Test
    @Throws(Exception::class)
    fun addGameShouldReturnTheSameJson() {
        val request = post("/games/add")
                .content(TestData.patxanga)
                .contentType("application/json")
                .header("Authorization", basicAuthHeader("user", "pwd"))

        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(TestData.patxanga, true))
    }

    @Test
    @Throws(Exception::class)
    fun addGameShouldCreateTimestampForGame() {
        val request = post("/games/add")
                .content(TestData.patxanga_no_timestamp)
                .contentType("application/json")
                .header("Authorization", basicAuthHeader("user", "pwd"))

        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(TimestampExists()))
    }

    @Test
    @Throws(Exception::class)
    fun addGameShouldSupportOptionalMetricsAndTags() {
        val request = post("/games/add")
                .content(TestData.mariokart)
                .contentType("application/json")
                .header("Authorization", basicAuthHeader("user", "pwd"))

        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(TestData.mariokart, false))
    }

    @Test
    @Throws(Exception::class)
    fun scoresShouldReturnAggregatedDataByAccount() {
        val expected = """
        {
            "availableMetrics": [
                "gols",
                "z.games",
                "z.result.lose",
                "z.result.win",
                "z.team.blaus",
                "z.team.grocs"
            ],
            "metrics": [
                {
                    "member": "arnau",
                    "metrics": {
                        "gols": 1,
                        "z.games": 1,
                        "z.result.win": 1,
                        "z.team.grocs": 1
                    }
                },
                {
                    "member": "guillem",
                    "metrics": {
                        "gols": 2,
                        "z.games": 1,
                        "z.result.lose": 1,
                        "z.team.blaus": 1
                    }
                },
                {
                    "member": "ramon",
                    "metrics": {
                        "gols": 2,
                        "z.games": 1,
                        "z.result.win": 1,
                        "z.team.grocs": 1
                    }
                },
                {
                    "member": "uri",
                    "metrics": {
                        "z.games": 1,
                        "z.result.lose": 1,
                        "z.team.blaus": 1
                    }
                }
            ],
            "scores": [
                {
                    "member": "ramon",
                    "score": 1
                },
                {
                    "member": "arnau",
                    "score": 1
                },
                {
                    "member": "uri",
                    "score": 0
                },
                {
                    "member": "guillem",
                    "score": 0
                }
            ]
        }

        """
        val request = get("/scores/patxanga/players")
                .header("Authorization", basicAuthHeader("user", "pwd"))

        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(expected))
    }

    @Test
    @Throws(Exception::class)
    fun slackCommandShouldSupportDoubleQuotes() {
        val request = post("/slack/command")
                .header("X-Slack-Signature", "fake")
                .header("X-Slack-Request-Timestamp", "123456789")
                .content("text=addgame+%E2%80%9Cbaby+mario%E2%80%9D+mario&user_name=mario&team_domain=scopely&channel_name=mario_kart&response_url=url")
                .contentType("application/x-www-form-urlencoded")

        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("""{"text":"Good game! A new game entry has been created!","attachments":[{"text":"1. baby mario\n2. mario"}],"response_type":"in_channel"}"""))
    }

    @Test
    @Throws(Exception::class)
    fun slackCommandShouldExecuteAsyncCommands() {
        val request = post("/slack/command")
                .header("X-Slack-Signature", "fake")
                .header("X-Slack-Request-Timestamp", "123456789")
                .content("text=addgame+%E2%80%9Cguillem%E2%80%9D+uri&user_name=mario&team_domain=patxanga&channel_name=patxanga&response_url=url")
                .contentType("application/x-www-form-urlencoded")

        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("""{"text":"Good game! A new game entry has been created!","attachments":[{"text":"1. guillem\n2. uri"}],"response_type":"in_channel"}"""))

        val asyncResponse = slackAsyncExecutorProviderForTesting.accumulatedResponses["url"]!!
        Assert.assertEquals("Computed ELO changes after this game", asyncResponse.text)
        Assert.assertEquals("1. guillem (1185, -15)\n2. uri (1185, -15)", asyncResponse.attachments[0].text)
    }

    @Test
    @Throws(Exception::class)
    fun slackCommandFailsIfEnvTokenIsNotPresent() {
        val request = post("/slack/command")
                .header("X-Slack-Signature", "fake")
                .header("X-Slack-Request-Timestamp", "123456789")
                .content("text=addgame+%E2%80%9Cbaby+mario%E2%80%9D+mario&user_name=mario&team_domain=company&channel_name=mario_kart&response_url=url")
                .contentType("application/x-www-form-urlencoded")

        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("""{"text":"Something went wrong!","attachments":[],"response_type":"ephemeral"}"""))
    }

    private fun basicAuthHeader(user: String, password: String): String {
        return "Basic " + Base64.getEncoder().encodeToString("$user:$password".toByteArray(Charset.defaultCharset()))
    }
}

class TimestampExists : BaseMatcher<String>() {
    override fun describeTo(description: Description?) {
        description!!.appendText("has a timestamp")
    }

    override fun matches(item: Any?): Boolean {
        if (item is String) {
            return ObjectMapper().readValue(item, Game::class.java).timestamp!! > 0
        }

        return false
    }
}


