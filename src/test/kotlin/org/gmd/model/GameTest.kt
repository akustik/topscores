package org.gmd.model

import org.gmd.TestData
import org.junit.Assert.*
import org.junit.Test

internal class GameTest {

    private val marioKart = TestData.mariokart()

    private val villager = Team("Villager")

    @Test
    fun findParty() {
        val actual = marioKart.getParty(villager)
        assertEquals(actual.score, 4)
    }

    @Test
    fun contains() {
        assertTrue(marioKart.contains(villager))
        assertFalse(marioKart.contains(Team("a fake team")))
    }
}