package org.gmd.command

import org.gmd.model.Game
import org.gmd.model.Party
import org.gmd.model.Team
import org.gmd.model.TeamMember
import org.gmd.service.GameService
import org.gmd.slack.SlackResponseHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

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
        val helper = SlackResponseHelper { run {} }

        Mockito.`when`(gameService.listGames(account, 1000)).thenReturn(listOf(addedGame))


        MatchUp(helper, gameService, account, tournament, "player").parse(listOf(player2, player1))

        Assert.assertEquals("From 1 games player2 beats player1 a 100%.", helper.slackResponse.text)
    }

    @Test
    fun testSlackResponseOnUnMatching() {
        val helper = SlackResponseHelper { run {} }

        Mockito.`when`(gameService.listGames(account, 1000)).thenReturn(listOf(addedGame))


        MatchUp(helper, gameService, account, tournament, "player").parse(listOf(player1, "unknown player"))

        Assert.assertEquals("No matches found", helper.slackResponse.text)
    }
}

