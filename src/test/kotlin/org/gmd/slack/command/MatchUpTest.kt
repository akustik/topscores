package org.gmd.slack.command

import org.gmd.TestData.Companion.dummy
import org.gmd.TestData.Companion.player1
import org.gmd.TestData.Companion.player2
import org.gmd.service.GameService
import org.gmd.slack.model.SlackResponse
import org.gmd.slack.SlackResponseHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest
class MatchUpTest {

    @MockBean
    lateinit var gameService: GameService

    private val account = "test"
    private val addedGame = dummy()

    @Test
    fun testSlackResponse() {

        val completableFuture = CompletableFuture<SlackResponse>()
        val matchUp = createTestMatchUp(completableFuture)

        matchUp.parse(listOf(player1, player2))

        val response = completableFuture.get(1, TimeUnit.MINUTES)
        Assert.assertEquals("player1 beats player2 100% of the times (sample: 1 games)", response.text)
    }

    @Test
    fun testSlackResponseOnWrongTournament() {
        val completableFuture = CompletableFuture<SlackResponse>()
        val matchUp = createTestMatchUp(completableFuture, "a fake tournament")

        matchUp.parse(listOf(player1, player2))

        val response = completableFuture.get(1, TimeUnit.MINUTES)
        Assert.assertEquals("No matches found", response.text)
    }

    @Test
    fun testSlackResponseOnUnMatching() {
        val completableFuture = CompletableFuture<SlackResponse>()
        val matchUp = createTestMatchUp(completableFuture)

        Mockito.`when`(gameService.listGames(account, 1000)).thenReturn(listOf(addedGame))

        matchUp.parse(listOf(player1, "unknown player"))

        val response = completableFuture.get(1, TimeUnit.MINUTES)
        Assert.assertEquals("No matches found", response.text)
    }

    private fun createTestMatchUp(completableFuture: CompletableFuture<SlackResponse>, matchUpTournament: String = addedGame.tournament): MatchUp {
        val helper = SlackResponseHelper { response -> completableFuture.complete(response) }

        Mockito.`when`(gameService.listGames(account, addedGame.tournament, 1000)).thenReturn(listOf(addedGame))
        return MatchUp(helper, gameService, account, matchUpTournament, "player")
    }
}

