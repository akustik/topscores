package org.gmd.model

import org.gmd.TestData
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GameTest {

    @Test
    fun findParty() {
        val actual = TestData.mariokart().getParty(Team("Villager"))
        assertEquals(actual.score, 4)
    }
}