package org.gmd.service

import org.gmd.Algorithm
import org.gmd.AsyncConfiguration
import org.gmd.TestData
import org.gmd.model.Evolution
import org.gmd.model.MemberMetrics
import org.gmd.model.Metric
import org.gmd.model.Score
import org.gmd.repository.GameRepository
import org.gmd.service.alg.AdderMemberRatingAlgorithm
import org.gmd.service.alg.ELOMemberRatingAlgorithm
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant


@RunWith(SpringRunner::class)
@SpringBootTest
class GameServiceImplTest {

    @Autowired
    lateinit var adderMemberRatingAlgorithm: AdderMemberRatingAlgorithm

    @Autowired
    lateinit var eloMemberRatingAlgorithm: ELOMemberRatingAlgorithm
    
    @MockBean
    lateinit var gameRepository: GameRepository
    
    fun buildGameService(): GameService {
        return GameServiceImpl(gameRepository, adderMemberRatingAlgorithm, eloMemberRatingAlgorithm)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldAddUpScoresForAllTeamMembers() {
        val gameService = buildGameService()
        val expected = listOf(
                Score("Ramon", 1), Score("Arnau", 1), Score("Uri", 0), Score("Guillem", 0)
        )
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val scores = gameService.computeTournamentMemberScores(account = account, tournament = tournament)

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldRateELOForAllTeamMembers() {
        val gameService = buildGameService()
        val expected = listOf(
                Score("Ramon", 1215), Score("Arnau", 1215), Score("Uri", 1185), Score("Guillem", 1185)
        )
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))
        
        val scores = gameService.computeTournamentMemberScores(account = account, tournament = tournament, alg = Algorithm.ELO)

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldRateELOForASingleTeamMember() {
        val gameService = buildGameService()
        val expected = Evolution("Ramon", listOf(1200, 1215))
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val scores = gameService.computeTournamentMemberScoreEvolution(account = account, tournament = tournament, player = listOf("Ramon"), alg = Algorithm.ELO).first()

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldRateELOEvolutionForASingleTeamMember() {
        val gameService = buildGameService()
        val expected = Evolution("Ramon", listOf(1200, 1215, 1229))
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val scores = gameService.computeTournamentMemberScoreEvolution(
                account = account,
                tournament = tournament,
                player = listOf("Ramon"),
                alg = Algorithm.ELO,
                withGames = listOf(TestData.patxanga())
        ).first()

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentMemberMetricsAggregateMetricsForAllTeamMembers() {
        val gameService = buildGameService()
        val expected = listOf(
                MemberMetrics(member="Arnau", metrics=listOf(Metric(name="gols", value=1), Metric(name="z.games", value=1), Metric(name="z.result.win", value=1), Metric(name="z.team.grocs", value=1))), 
                MemberMetrics(member="Guillem", metrics=listOf(Metric(name="gols", value=2), Metric(name="z.games", value=1), Metric(name="z.result.lose", value=1), Metric(name="z.team.blaus", value=1))), 
                MemberMetrics(member="Ramon", metrics=listOf(Metric(name="gols", value=2), Metric(name="z.games", value=1), Metric(name="z.result.win", value=1), Metric(name="z.team.grocs", value=1))), 
                MemberMetrics(member="Uri", metrics=listOf(Metric(name="z.games", value=1), Metric(name="z.result.lose", value=1), Metric(name="z.team.blaus", value=1)))
        )

        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val metrics = gameService.computeTournamentMemberMetrics(account = account, tournament = tournament)

        Assert.assertEquals(expected.toString(), metrics.toString())
    }
}

