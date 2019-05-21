package org.gmd.repository

import org.gmd.model.Game
import org.gmd.repository.jdbc.GameRepository

class GameRepositoryForTesting(val games: List<Game>): GameRepository {
    
    override fun listGames(account: String): List<Game> {
        return games.filter { g -> g.account.equals(account) }
    }

    override fun addGame(game: Game): Game {
        return game
    }
    
    override fun listGames(): List<Game> {
        return games
    }
}