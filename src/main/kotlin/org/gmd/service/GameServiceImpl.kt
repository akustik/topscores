package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.*
import org.gmd.repository.GameRepository
import org.gmd.service.alg.AdderMemberRatingAlgorithm
import org.gmd.service.alg.ELOMemberRatingAlgorithm
import org.gmd.service.alg.MemberRatingAlgorithm
import org.springframework.stereotype.Component

@Component
open class GameServiceImpl(val repository: GameRepository,
                           val adderAlg: AdderMemberRatingAlgorithm,
                           val eloAlg: ELOMemberRatingAlgorithm) : GameService {
    
    override fun addGame(account: String, game: Game): Game = repository.addGame(account, game)

    override fun listGames(account: String): List<Game> = repository.listGames(account)

    override fun computeTournamentMemberScores(account: String, tournament: String, alg: Algorithm): List<Score> {
        val games = repository.listGames(account = account, tournament = tournament)
        return descendent(raterFor(alg).rate(games))
    }

    override fun computeTournamentMemberScoreEvolution(account: String, tournament: String, player: String, alg: Algorithm): List<Score> {
        val games = repository.listGames(account = account, tournament = tournament)
        val rater = raterFor(alg) 
        
        return (1..games.size).flatMap { 
            i -> rater.rate(games.subList(0, i)).filter { s -> s.member.equals(player)  }
        }
    }
    
    private fun raterFor(alg: Algorithm): MemberRatingAlgorithm {
        return when (alg) {
            Algorithm.SUM -> adderAlg
            Algorithm.ELO -> eloAlg
            else -> adderAlg
        }
    }

    private fun descendent(scores: List<Score>): List<Score> {
        return scores.toSortedSet(compareBy({ score -> score.score }, { score -> score.member })).reversed()
    }

    override fun computeTournamentMemberMetrics(account: String, tournament: String): List<MemberMetrics> {
        val games = repository.listGames(account = account, tournament = tournament)
        val metricsById = games
                .flatMap { it.parties }
                .flatMap { it.metrics + defaultMetricsForMembers(it.members, it.team.name)}
                .filter { it.name.contains(":") }
                .groupingBy { it.name }

        return metricsById.reduce { _, a, b ->
            run {
                Metric(a.name, a.value + b.value)
            }
        }.map {
            Pair(it.key.substringAfter(":"), Metric(it.key.substringBefore(":"), it.value.value))
        }.groupBy({ it.first }, { it.second }).map { MemberMetrics(it.key, it.value.sortedBy { it.name }) }.sortedBy { it.member }
    }
    
    private fun defaultMetricsForMembers(members: List<TeamMember>, teamName: String): List<Metric> {
        return members.flatMap { m -> listOf(metricForPlayer("played", m.name), metricForPlayer(teamName, m.name)) }
    }
    
    private fun metricForPlayer(metric: String, player: String, value: Int = 1) = Metric("$metric:$player", value)

    override fun listTournaments(account: String): List<String> {
        return repository.listTournaments(account).sorted()
    }

}