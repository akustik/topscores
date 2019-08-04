package org.gmd.command

import org.gmd.Algorithm
import org.gmd.EnvProviderForTesting
import org.gmd.TestData.Companion.dummy
import org.gmd.TestData.Companion.player1
import org.gmd.TestData.Companion.player2
import org.gmd.model.Evolution
import org.gmd.service.AsyncGameServiceForTesting
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

@RunWith(SpringRunner::class)
@SpringBootTest
class AddGameTest {

    @MockBean
    lateinit var gameService: GameService

    val account = "test"
    val addedGame = dummy()

    @Test
    @Throws(Exception::class)
    fun addGameShouldPrintFeedbackOfTheAddedGame() {
        val helper = SlackResponseHelper({ _ -> run {} })

        //TODO: Give it a try to mockito kotlin and see if it works better with mocks :_(
        Mockito.`when`(gameService.addGame(account, addedGame)).thenReturn(addedGame)

        AddGame(response = helper,
                envProvider = EnvProviderForTesting(emptyMap(), addedGame.timestamp!!),
                service = gameService,
                asyncService = AsyncGameServiceForTesting(gameService),
                account = account,
                tournament = addedGame.tournament)
                .parse(listOf(player1, player2))

        Assert.assertEquals("Good game! A new game entry has been created!", helper.slackResponse!!.text)
        Assert.assertEquals("1. player1\n2. player2", helper.slackResponse!!.attachments.first().text)
        Assert.assertEquals("in_channel", helper.slackResponse!!.responseType)
    }

    @Test
    @Throws(Exception::class)
    fun addGameShouldPrintEloEvolutionIfRequired() {
        var asyncResponse: SlackResponse? = null
        val helper = SlackResponseHelper { response ->
            run {
                asyncResponse = response
            }
        }
        val evolutions = listOf(
                Evolution(player1, listOf(1200, 1213)),
                Evolution(player2, listOf(1200, 1193))
        )

        Mockito.`when`(gameService.addGame(account, addedGame)).thenReturn(addedGame)
        Mockito.`when`(gameService.computeTournamentMemberScoreEvolution(
                account = account,
                tournament = addedGame.tournament,
                alg = Algorithm.ELO,
                player = listOf(player1, player2))).thenReturn(evolutions)


        AddGame(response = helper,
                envProvider = EnvProviderForTesting(emptyMap(), addedGame.timestamp!!),
                service = gameService,
                asyncService = AsyncGameServiceForTesting(gameService),
                account = account,
                tournament = addedGame.tournament)
                .parse(listOf(player1, player2))

        Assert.assertEquals("Good game! A new game entry has been created!", helper.slackResponse!!.text)
        Assert.assertEquals("1. player1\n2. player2", helper.slackResponse!!.attachments.first().text)
        Assert.assertEquals("1. player1 (1213, +13)\n2. player2 (1193, -7)", asyncResponse!!.attachments.first().text)
        Assert.assertEquals("in_channel", helper.slackResponse!!.responseType)

    }
}

