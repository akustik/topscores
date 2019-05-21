package org.gmd

import org.gmd.model.Game

class GameRepositoryForTesting: GameRepository {
    override fun addGame(game: Game): Game {
        return game
    }
    
    override fun listGames(): List<Game> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}