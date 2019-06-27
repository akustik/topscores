package org.gmd.service

import org.gmd.Algorithm
import org.gmd.PartyKind
import org.gmd.model.*
import org.gmd.repository.GameRepository
import org.gmd.service.alg.AdderMemberRatingAlgorithm
import org.gmd.service.alg.ELOMemberRatingAlgorithm
import org.gmd.service.alg.MemberRatingAlgorithm
import org.springframework.stereotype.Component
import java.time.Instant

@Component
open class GameServiceImpl(val repository: GameRepository,
                           val adderAlg: AdderMemberRatingAlgorithm,
                           val eloAlg: ELOMemberRatingAlgorithm) : GameService {

    override fun addGame(account: String, game: Game): Game = repository.addGame(account, game)

    override fun listGames(account: String): List<Game> = repository.listGames(account).map { e -> e.second }

    override fun computeTournamentMemberScores(account: String, tournament: String, alg: Algorithm): List<Score> {
        val games = repository.listGames(account = account, tournament = tournament).map { e -> e.second }
        return descendent(raterFor(alg).rate(games))
    }

    override fun computeTournamentMemberScoreEvolution(account: String, tournament: String, player: List<String>, alg: Algorithm, withGames: List<Game>): List<Evolution> {
        val games = repository.listGames(account = account, tournament = tournament).map { e -> e.second }
        return raterFor(alg).evolution(games + withGames).filter { s -> player.contains(s.member) }
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
        val games = repository.listGames(account = account, tournament = tournament).map { e -> e.second }
        val metricsById = games
                .flatMap { game -> withPartyKind(game)}
                .flatMap { it.second.metrics + defaultMetricsForMembers(it.second.members, it.second.team.name, it.first)}
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
    
    private fun withPartyKind(game: Game): List<Pair<PartyKind, Party>> {
        val scores = game.parties.map { p -> p.score }
        val maxScore = scores.max()
        val maxKind = if(scores.filter { s -> s != maxScore }.isEmpty()) PartyKind.TIE else PartyKind.WIN
        return game.parties.map { p -> Pair(if(p.score == maxScore) maxKind else PartyKind.LOSE, p) }
    }
    
    private fun defaultMetricsForMembers(members: List<TeamMember>, teamName: String, kind: PartyKind): List<Metric> {
        return members.flatMap { m -> listOf(
                metricForPlayer("z.games", m.name), 
                metricForPlayer("z.team.$teamName", m.name),
                metricForPlayer("z.result.${kind.name.toLowerCase()}", m.name)
        ) }
    }
    
    private fun metricForPlayer(metric: String, player: String, value: Int = 1) = Metric("$metric:$player", value)

    override fun listTournaments(account: String): List<String> {
        return repository.listTournaments(account).sorted()
    }

    override fun listEntries(account: String, tournament: String): List<Pair<Instant, Game>> {
        return repository.listGames(account, tournament).sortedByDescending { e -> e.first }
    }
    
    override fun deleteEntry(account: String, tournament: String, createdAt: Instant): Boolean {
        return repository.deleteGame(account, tournament, createdAt)
    }
}