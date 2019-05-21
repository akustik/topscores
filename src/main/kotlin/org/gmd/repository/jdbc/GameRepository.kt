package org.gmd.repository.jdbc

import org.gmd.model.Game

interface GameRepository {

    fun listGames(): List<Game>

    fun listGames(account: String): List<Game>
    
    fun addGame(game: Game): Game
}