package com.example

import com.example.model.Game

interface GameRepository {

    fun listGames(): List<Game>
    
    fun addGame(game: Game): Game
}