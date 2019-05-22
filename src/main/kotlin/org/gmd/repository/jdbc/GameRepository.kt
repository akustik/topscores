package org.gmd.repository.jdbc

import org.gmd.model.Game

interface GameRepository {

    fun listGames(account: String): List<Game>

    fun listGames(account: String, tournament: String): List<Game>
    
    fun addGame(account: String, game: Game): Game
}