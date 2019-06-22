package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.*
import java.sql.Timestamp


interface GameService {

    fun listGames(account: String): List<Game>

    fun addGame(account: String, game: Game): Game

    fun computeTournamentMemberScores(account: String, tournament: String, alg: Algorithm = Algorithm.SUM): List<Score>
    
    fun computeTournamentMemberScoreEvolution(account: String, tournament: String, player: String, alg: Algorithm = Algorithm.SUM): Evolution

    fun computeTournamentMemberMetrics(account: String, tournament: String): List<MemberMetrics>
    
    fun listTournaments(account: String): List<String>
    
    fun listEntries(account: String, tournament: String): List<Pair<Timestamp, Game>>
    
    fun deleteEntry(account: String, tournament: String, createdAt: Timestamp): Boolean
}