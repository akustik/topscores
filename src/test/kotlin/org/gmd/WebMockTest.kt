package org.gmd

import com.fasterxml.jackson.databind.ObjectMapper
import org.gmd.model.Game
import org.gmd.model.Score
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.charset.Charset


@WebMvcTest(Controller::class)
@RunWith(SpringRunner::class)
class WebMockTest {

    companion object {
        val patxanga = WebMockTest::class.java.getResource("/samples/patxanga.json").readText()
        val patxanga_no_timestamp = WebMockTest::class.java.getResource("/samples/patxanga_no_timestamp.json").readText()
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

        @Bean
        open fun gameRepository(): GameRepository {
            val game: Game = Game.fromJsonBytes(WebMockTest.patxanga.toByteArray(Charset.defaultCharset()))
            return GameRepositoryForTesting(listOf(game))
        }

        @Bean
        open fun controller(): Controller {
            return Controller()
        }
    }

    @Test
    @Throws(Exception::class)
    fun addGameShouldReturnTheSameJson() {
        var request = post("/games/add").content(patxanga).contentType("application/json")
        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(patxanga, true))
    }

    @Test
    @Throws(Exception::class)
    fun addGameShouldCreateTimestampForGame() {
        var request = post("/games/add").content(patxanga_no_timestamp).contentType("application/json")
        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(TimestampExists()))
    }

    @Test
    @Throws(Exception::class)
    fun scoresShouldReturnAggregatedDataByAccount() {
        var expected = listOf(
                Score("Ramon", 2), Score("Arnau", 2), Score("Uri", 1), Score("Guillem", 1)
        )
        var request = get("/scores/patxanga")
        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(ObjectMapper().writeValueAsString(expected)))
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


