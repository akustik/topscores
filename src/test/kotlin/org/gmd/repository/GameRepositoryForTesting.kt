package org.gmd.repository

import org.gmd.model.Game
import java.time.Instant

class GameRepositoryForTesting(val accountGames: List<Pair<Instant, Game>>) : GameRepository {

    override fun listTournaments(account: String): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listGames(account: String, tournament: String): List<Pair<Instant, Game>> {
        return accountGames.filter { g -> g.second.tournament.equals(tournament) }
    }

    override fun addGame(account: String, game: Game): Game {
        return game
    }

    override fun listGames(account: String): List<Pair<Instant, Game>> {
        return accountGames
    }

    override fun deleteGame(account: String, tournament: String, createdAt: Instant): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}