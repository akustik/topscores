package org.gmd.service

import org.gmd.TestData
import org.gmd.model.Score
import org.gmd.repository.jdbc.GameRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
class GameServiceImplTest {

    @TestConfiguration
    open class ServiceContext {

        @Bean
        open fun gameService(): GameService {
            return GameServiceImpl()
        }
    }

    @Autowired
    lateinit var gameService: GameService

    @MockBean
    lateinit var gameRepository: GameRepository

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldAddUpScoresForAllTeamMembers() {
        val expected = listOf(
                Score("Ramon", 2), Score("Arnau", 2), Score("Uri", 1), Score("Guillem", 1)
        )
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament)).thenReturn(listOf(TestData.patxanga()))

        val scores = gameService.computeTournamentScores(account = account, tournament = tournament)
        
        Assert.assertEquals(expected, scores)
    }
}

