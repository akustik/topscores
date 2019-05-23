package org.gmd.service

import org.gmd.Algorithm
import org.gmd.TestData
import org.gmd.model.Score
import org.gmd.repository.GameRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest
class GameServiceImplTest {

    @Autowired
    lateinit var gameService: GameServiceImpl

    @MockBean
    lateinit var gameRepository: GameRepository

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldAddUpScoresForAllTeamMembers() {
        val expected = listOf(
                Score("Ramon", 1), Score("Arnau", 1), Score("Uri", 0), Score("Guillem", 0)
        )
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament)).thenReturn(listOf(TestData.patxanga()))

        val scores = gameService.computeTournamentScores(account = account, tournament = tournament)
        
        Assert.assertEquals(expected, scores)
    }
    
    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldComputeELOForAllTeamMembers() {
        val expected = listOf(
                Score("Ramon", 1215), Score("Arnau", 1215), Score("Uri", 1185), Score("Guillem", 1185)
        )
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament)).thenReturn(listOf(TestData.patxanga()))

        val scores = gameService.computeTournamentScores(account = account, tournament = tournament, alg = Algorithm.ELO)

        Assert.assertEquals(expected, scores)        
    }
}

