package org.gmd.repository

import org.gmd.model.Game
import java.time.Instant

interface GameRepository {

    fun listGames(account: String, maxElements: Int): List<Pair<Instant, Game>>

    fun listGames(account: String, tournament: String, maxElements: Int): List<Pair<Instant, Game>>
    
    fun addGame(account: String, game: Game): Game

    fun deleteGame(account: String, tournament: String, createdAt: Instant): Boolean
    
    fun listTournaments(account: String): List<String>
}