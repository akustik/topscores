package org.gmd.command

import org.gmd.model.Game
import org.gmd.model.Party
import org.gmd.model.Team
import org.gmd.model.TeamMember
import org.gmd.service.GameService
import org.gmd.slack.SlackResponse
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

    val player1 = "player1"
    val player2 = "player2"
    val account = "test"
    val tournament = "patxanga"
    val timestamp = 12345L
    val addedGame = Game(
            tournament = tournament,
            parties = listOf(
                    Party(
                            team = Team(player2),
                            members = listOf(TeamMember(player2)),
                            metrics = listOf(),
                            tags = listOf(),
                            score = 1
                    ),
                    Party(
                            team = Team(player1),
                            members = listOf(TeamMember(player1)),
                            metrics = listOf(),
                            tags = listOf(),
                            score = 2
                    )
            ),
            timestamp = timestamp
    )

    @Test
    fun testSlackResponse() {

        val completableFuture = CompletableFuture<SlackResponse>()
        val matchUp = createTestMatchUp(completableFuture)

        matchUp.parse(listOf(player1, player2))

        val response = completableFuture.get(1, TimeUnit.MINUTES)
        Assert.assertEquals("From 1 games player1 beats player2 a 100%.", response.text)
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

    private fun createTestMatchUp(completableFuture: CompletableFuture<SlackResponse>, matchUpTournament: String = tournament): MatchUp {
        val helper = SlackResponseHelper { response -> completableFuture.complete(response) }

        Mockito.`when`(gameService.listGames(account, tournament, 1000)).thenReturn(listOf(addedGame))
        return MatchUp(helper, gameService, account, matchUpTournament, "player")
    }
}

