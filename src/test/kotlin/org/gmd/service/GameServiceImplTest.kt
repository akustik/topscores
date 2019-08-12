package org.gmd.service

import org.gmd.Algorithm
import org.gmd.TestData
import org.gmd.model.Evolution
import org.gmd.model.MemberMetrics
import org.gmd.model.Metric
import org.gmd.model.Score
import org.gmd.repository.GameRepository
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
    lateinit var gameService: GameServiceImpl

    @MockBean
    lateinit var gameRepository: GameRepository

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldAddUpScoresForAllTeamMembers() {
        val expected = listOf(
                Score("ramon", 1, 1), Score("arnau", 1, 1), Score("uri", 0, 1), Score("guillem", 0, 1)
        )
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament, maxElements = 1000)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val scores = gameService.computeTournamentMemberScores(account = account, tournament = tournament)

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldFilterByTeam() {
        val expected = listOf(
                Score("Guillem", 4, 1), Score("Ciriano", 3, 1)
        )
        val account = "test"
        val tournament = "mariokart"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament, maxElements = 1000)).thenReturn(listOf(Pair(Instant.now(), TestData.mariokart())))

        val scores = gameService.computeTournamentMemberScores(account = account, tournament = tournament, teams = listOf("Villager", "Luigi"))

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldRateELOForAllTeamMembers() {
        val expected = listOf(
                Score("ramon", 1215, 1), Score("arnau", 1215, 1), Score("uri", 1185, 1), Score("guillem", 1185, 1)
        )
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament, maxElements = 1000)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val scores = gameService.computeTournamentMemberScores(account = account, tournament = tournament, alg = Algorithm.ELO)

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldRateELOForASingleTeamMember() {
        val expected = Evolution("ramon", listOf(Pair(1200, 0L), Pair(1215, 1558211897715L)))
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament, maxElements = 1000)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val scores = gameService.computeTournamentMemberScoreEvolution(account = account, tournament = tournament, player = listOf("ramon"), alg = Algorithm.ELO).first()

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentScoresShouldRateELOEvolutionForASingleTeamMember() {
        val expected = Evolution("ramon", listOf(Pair(1200, 0L), Pair(1215, 1558211897715L), Pair(1230, 1558211897715L)))
        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament, maxElements = 1000)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val scores = gameService.computeTournamentMemberScoreEvolution(
                account = account,
                tournament = tournament,
                player = listOf("ramon"),
                alg = Algorithm.ELO,
                withGames = listOf(TestData.patxanga())
        ).first()

        Assert.assertEquals(expected, scores)
    }

    @Test
    @Throws(Exception::class)
    fun computeTournamentMemberMetricsAggregateMetricsForAllTeamMembers() {
        val expected = listOf(
                MemberMetrics(member = "arnau", metrics = listOf(Metric(name = "gols", value = 1), Metric(name = "z.games", value = 1), Metric(name = "z.result.win", value = 1), Metric(name = "z.team.grocs", value = 1))),
                MemberMetrics(member = "guillem", metrics = listOf(Metric(name = "gols", value = 2), Metric(name = "z.games", value = 1), Metric(name = "z.result.lose", value = 1), Metric(name = "z.team.blaus", value = 1))),
                MemberMetrics(member = "ramon", metrics = listOf(Metric(name = "gols", value = 2), Metric(name = "z.games", value = 1), Metric(name = "z.result.win", value = 1), Metric(name = "z.team.grocs", value = 1))),
                MemberMetrics(member = "uri", metrics = listOf(Metric(name = "z.games", value = 1), Metric(name = "z.result.lose", value = 1), Metric(name = "z.team.blaus", value = 1)))
        )

        val account = "test"
        val tournament = "patxanga"
        Mockito.`when`(gameRepository.listGames(account = account, tournament = tournament, maxElements = 1000)).thenReturn(listOf(Pair(Instant.now(), TestData.patxanga())))

        val metrics = gameService.computeTournamentMemberMetrics(account = account, tournament = tournament)

        Assert.assertEquals(expected.toString(), metrics.toString())
    }
}

