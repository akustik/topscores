package org.gmd.repository

import org.gmd.model.Game
import org.gmd.repository.jdbc.GameRepository

class GameRepositoryForTesting(val accountGames: List<Game>) : GameRepository {
    override fun listGames(account: String, tournament: String): List<Game> {
        return accountGames.filter { g -> g.tournament.equals(tournament) }
    }

    override fun addGame(account: String, game: Game): Game {
        return game
    }

    override fun listGames(account: String): List<Game> {
        return accountGames
    }
}