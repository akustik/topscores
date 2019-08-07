package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.MemberMetrics
import org.gmd.model.Score
import java.time.Instant


interface GameService {

    fun listGames(account: String, maxElements: Int = 5): List<Game>

    fun listGames(account: String, tournament: String, maxElements: Int = 5): List<Game>

    fun addGame(account: String, game: Game): Game

    fun computeTournamentMemberScores(account: String, tournament: String, alg: Algorithm = Algorithm.SUM, teams: List<String> = emptyList()): List<Score>

    fun computeTournamentMemberScoreEvolution(account: String, tournament: String, player: List<String> = emptyList(), alg: Algorithm = Algorithm.SUM, withGames: List<Game> = emptyList()): List<Evolution>

    fun computeTournamentMemberMetrics(account: String, tournament: String): List<MemberMetrics>

    fun listTournaments(account: String): List<String>

    fun listEntries(account: String, tournament: String, maxElements: Int = 5): List<Pair<Instant, Game>>

    fun deleteEntry(account: String, tournament: String, createdAt: Instant): Boolean
}