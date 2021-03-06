package org.gmd.slack.model

import org.gmd.util.JsonUtils
import org.junit.Assert
import org.junit.Test

class SlackTeamAuthTest {

    private val example = """
    {
        "access_token": "fake",
        "ok": true,
        "scope": "identify,commands",
        "team_id": "EFDG",
        "team_name": "patxanga",
        "user_id": "ABCD"
    }        
    """

    @Test
    fun shouldDeserialize() {
        val o = JsonUtils.readValue(example, SlackTeamAuth::class.java)
        Assert.assertEquals(true, o.ok)
        Assert.assertEquals("identify,commands", o.scope)
        Assert.assertEquals("fake", o.accessToken)
        Assert.assertEquals("EFDG", o.teamId)
        Assert.assertEquals("patxanga", o.teamName)
        Assert.assertEquals("ABCD", o.userId)
    }
}