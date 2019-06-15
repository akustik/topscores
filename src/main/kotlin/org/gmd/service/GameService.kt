package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.*


interface GameService {

    fun listGames(account: String): List<Game>

    fun addGame(account: String, game: Game): Game

    fun computeTournamentMemberScores(account: String, tournament: String, alg: Algorithm = Algorithm.SUM): List<Score>
    
    fun computeTournamentMemberScoreEvolution(account: String, tournament: String, player: String, alg: Algorithm = Algorithm.SUM): Evolution

    fun computeTournamentMemberMetrics(account: String, tournament: String): List<MemberMetrics>
    
    fun listTournaments(account: String): List<String>
}