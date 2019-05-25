package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.Game
import org.gmd.model.MemberMetrics
import org.gmd.model.Metric
import org.gmd.model.Score
import org.gmd.repository.GameRepository
import org.gmd.service.alg.AdderMemberRatingAlgorithm
import org.gmd.service.alg.ELOMemberRatingAlgorithm
import org.springframework.stereotype.Component

@Component
open class GameServiceImpl(val repository: GameRepository,
                           val adderAlg: AdderMemberRatingAlgorithm,
                           val eloAlg: ELOMemberRatingAlgorithm) : GameService {
    override fun addGame(account: String, game: Game): Game = repository.addGame(account, game)

    override fun listGames(account: String): List<Game> = repository.listGames(account)

    override fun computeTournamentMemberScores(account: String, tournament: String, alg: Algorithm): List<Score> {
        val games = repository.listGames(account = account, tournament = tournament)
        return when (alg) {
            Algorithm.SUM -> descendent(adderAlg.rate(games))
            Algorithm.ELO -> descendent(eloAlg.rate(games))
            else -> adderAlg.rate(games)
        }
    }

    private fun descendent(scores: List<Score>): List<Score> {
        return scores.toSortedSet(compareBy({ score -> score.score }, { score -> score.member })).reversed()
    }

    override fun computeTournamentMemberMetrics(account: String, tournament: String): List<MemberMetrics> {
        val games = repository.listGames(account = account, tournament = tournament)
        val metricsById = games
                .flatMap { it.parties }
                .flatMap { it.metrics }
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

    override fun listTournaments(account: String): List<String> {
        return repository.listTournaments(account).sorted()
    }

}