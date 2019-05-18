package com.example

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
    fun greetingShouldReturnMessageFromService() {
        //`when`(service!!.greet()).thenReturn("Hello Mock")
        val patxanga = WebMockTest::class.java.getResource("/samples/patxanga.json").readText()
        var request = post("/games/add").content(patxanga).contentType("application/json")
        this.mockMvc!!.perform(request).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(patxanga, true))
        
        
    }
}


