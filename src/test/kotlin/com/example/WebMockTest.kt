package com.example

import com.example.model.Game
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(Controller::class)
@RunWith(SpringRunner::class)
class WebMockTest {

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
            return GameRepositoryForTesting()
        }

        @Bean
        open fun controller(): Controller {
            return Controller()
        }
    }

    @Test
    @Throws(Exception::class)
    fun addGameShouldReturnTheSameJson() {
        val patxanga = WebMockTest::class.java.getResource("/samples/patxanga.json").readText()
        var request = post("/games/add").content(patxanga).contentType("application/json")
        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(patxanga, true))
    }

    @Test
    @Throws(Exception::class)
    fun addGameShouldCreateTimestampForGame() {
        val patxanga = WebMockTest::class.java.getResource("/samples/patxanga_no_timestamp.json").readText()
        var request = post("/games/add").content(patxanga).contentType("application/json")
        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(TimestampExists()))

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


