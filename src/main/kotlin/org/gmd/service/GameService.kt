package org.gmd.service

import org.gmd.model.Game
import org.gmd.model.Score


interface GameService {

    fun listGames(): List<Game>
    
    fun addGame(game: Game): Game
    
    fun getAccountScores(account: String): List<Score>
}