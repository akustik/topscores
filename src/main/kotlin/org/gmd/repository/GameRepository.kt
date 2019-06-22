package org.gmd.repository

import org.gmd.model.Game
import java.sql.Timestamp

interface GameRepository {

    fun listGames(account: String): List<Pair<Timestamp, Game>>

    fun listGames(account: String, tournament: String): List<Pair<Timestamp, Game>>
    
    fun addGame(account: String, game: Game): Game

    fun deleteGame(account: String, tournament: String, createdAt: Timestamp): Boolean
    
    fun listTournaments(account: String): List<String>
}