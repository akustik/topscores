package org.gmd.slack

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

class SlackTeamAuthTest {
    
    private val example = """
        {
            "ok": true,
            "access_token": "xoxp-1111827399-16111519414-20367011469-5f89a31i07",
            "scope": "identity.basic",
            "team_id": "T0G9PQBBK"
        }
        """

    @Test
    fun shouldDeserialize() {   
        val o = ObjectMapper().readValue(example, SlackTeamAuth::class.java)
        Assert.assertEquals(true, o.ok)
        Assert.assertEquals("identity.basic", o.scope)
        Assert.assertEquals("xoxp-1111827399-16111519414-20367011469-5f89a31i07", o.accessToken)
        Assert.assertEquals("T0G9PQBBK", o.teamId)
    }
}