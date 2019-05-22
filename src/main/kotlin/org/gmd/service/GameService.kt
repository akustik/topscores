package org.gmd.service

import org.gmd.model.Game
import org.gmd.model.Score


interface GameService {

    fun listGames(account: String): List<Game>
    
    fun addGame(account: String, game: Game): Game
    
    fun computeTournamentScores(account: String, tournament: String): List<Score>
}