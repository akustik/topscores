package org.gmd.model

import org.gmd.TestData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*


class TeamMatchUpTest {

    @Test
    fun calculateWinRatio() {
        val games = Collections.singletonList(TestData.mariokart())
        val villager = Team("Villager")
        val player = Team("Player")

        val winRatio = TeamMatchUp(villager, player, games).calculateWinRatio()

        val inverseWinRatio = TeamMatchUp(player, villager, games).calculateWinRatio()

        assertEquals(100, winRatio)
        assertEquals(0, inverseWinRatio)
    }
}