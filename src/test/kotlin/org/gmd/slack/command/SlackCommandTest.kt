package org.gmd.slack.command

import org.junit.Assert
import org.junit.Test

class SlackCommandTest: SlackCommand {

    @Test
    fun normalizePlayersShouldSupportMultiPlayerTeams() {
        val res = normalizePlayers(listOf("player1", "player2", ",", "player3"))

        Assert.assertEquals(listOf(listOf("player1", "player2"), listOf("player3")), res)
    }

    @Test
    fun normalizePlayersShouldSupportSinglePlayerTeams() {
        val res = normalizePlayers(listOf("player1", "player2", "player3"))

        Assert.assertEquals(listOf(listOf("player1"), listOf("player2"), listOf("player3")), res)
    }
}
