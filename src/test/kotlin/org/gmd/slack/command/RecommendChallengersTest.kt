package org.gmd.slack.command

import org.gmd.Algorithm
import org.gmd.TestData.Companion.mariokart
import org.gmd.model.Score
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper
import org.gmd.slack.model.SlackResponse
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
class RecommendChallengersTest {

    @MockBean
    lateinit var gameService: GameService

    val account = "test"
    val addedGame = mariokart()

    @Test
    fun testSlackResponseOnLowerElo() {

        val completableFuture = CompletableFuture<SlackResponse>()
        val recommendChallengers = createTestRecommendChallenger(completableFuture)

        recommendChallengers.parse(listOf("Villager", "--min", "1"))

        val response = completableFuture.get(1, TimeUnit.MINUTES)
        Assert.assertEquals("Best challenges for Villager", response.text)
        Assert.assertEquals("1. Wario\n2. Player\n3. Luigi", response.attachments.get(0).text)
    }

    @Test
    fun testSlackResponseOnLuigii() {

        val completableFuture = CompletableFuture<SlackResponse>()
        val recommendChallengers = createTestRecommendChallenger(completableFuture)

        recommendChallengers.parse(listOf("Luigi", "--min", "1"))

        val response = completableFuture.get(1, TimeUnit.MINUTES)
        Assert.assertEquals("1. Wario\n2. Player\n3. Villager", response.attachments.get(0).text)
    }

    @Test
    fun testSlackResponseOnPlayer() {

        val completableFuture = CompletableFuture<SlackResponse>()
        val recommendChallengers = createTestRecommendChallenger(completableFuture)

        recommendChallengers.parse(listOf("Player", "--min", "1"))

        val response = completableFuture.get(1, TimeUnit.MINUTES)
        Assert.assertEquals("1. Wario\n2. Luigi\n3. Villager", response.attachments.get(0).text)
    }


    @Test
    fun testPlayerNotFound() {

        val completableFuture = CompletableFuture<SlackResponse>()
        val recommendChallengers = createTestRecommendChallenger(completableFuture)

        recommendChallengers.parse(listOf("sddgfhdsfg", "--min", "1"))

        val response = completableFuture.get(1, TimeUnit.MINUTES)
        Assert.assertEquals("Not found player sddgfhdsfg in list", response.text)
        Assert.assertEquals("Villager\nLuigi\nWario\nPlayer\nDarks", response.attachments.get(0).text)
    }

    private fun createTestRecommendChallenger(completableFuture: CompletableFuture<SlackResponse>, tournament: String = addedGame.tournament): RecommendChallengers {
        val helper = SlackResponseHelper { response -> completableFuture.complete(response) }

        Mockito.`when`(gameService.listGames(account, addedGame.tournament, 1000)).thenReturn(listOf(addedGame))
        val playerScores = listOf(Score("Villager", 1177, 1)
                , Score("Luigi", 1205, 1)
                , Score("Wario", 1311, 1)
                , Score("Player", 1248, 1)
                , Score("Darks", 1550, 1))
        Mockito.`when`(gameService.computeTournamentMemberScores(account, addedGame.tournament, Algorithm.ELO)).thenReturn(playerScores)
        return RecommendChallengers(helper, gameService, account, tournament, "player")
    }
}

