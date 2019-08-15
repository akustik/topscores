package org.gmd.util

import org.gmd.model.Game
import org.gmd.model.Team

typealias WinRatio = Pair<Int, Double>

fun calculateWinRatioForA(games: List<Game>, a: Team, b: Team): WinRatio? {
    val matchingGames = games.filter { game -> game.contains(a) && game.contains(b) }
    
    return if (matchingGames.isNotEmpty()) {
        Pair(matchingGames.size, calculateWinRatioForGames(a, b, matchingGames))
    } else {
        null
    }
}

private fun calculateWinRatioForGames(a: Team, b: Team, games: List<Game>): Double {
    val wins = games.filter { game -> game.getParty(a).score > game.getParty(b).score }
            .count()
    return wins.toDouble() / games.size.toDouble()
}