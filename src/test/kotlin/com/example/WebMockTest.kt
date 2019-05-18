package com.example

import com.example.model.Game
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print


@WebMvcTest(Controller::class)
@RunWith(SpringRunner::class)
class WebMockTest {

    @Autowired
    private val mockMvc: MockMvc? = null

    //@MockBean private val service: Controller? = null

    @Test
    @Throws(Exception::class)
    fun addGameShouldReturnTheSameJson() {
        //`when`(service!!.greet()).thenReturn("Hello Mock")
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

class TimestampExists: BaseMatcher<String>() {
    override fun describeTo(description: Description?) {
        description!!.appendText("has a timestamp")
    }

    override fun matches(item: Any?): Boolean {
        if(item is String) {
            return ObjectMapper().readValue(item, Game::class.java).timestamp!! > 0
        }
        
        return false
    }
}


