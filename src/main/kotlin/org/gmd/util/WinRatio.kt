package org.gmd.util

import org.gmd.model.Game
import org.gmd.model.Team
import java.util.*

fun calculateWinRatio(games: List<Game>, team1: Team, team2: Team): Pair<Int, Optional<Int>> {
    val matchingGames = games.filter { game -> game.contains(team1) && game.contains(team2) }

    val total = matchingGames.size
    var winRatio = Optional.empty<Int>();
    if (total > 0) {
        winRatio = Optional.of(calculateWinRatio(team1, team2, matchingGames));
    }
    return Pair(total, winRatio)
}

fun calculateWinRatio(team1: Team, team2: Team, games: List<Game>): Int {
    val wins = games.filter { game -> game.getParty(team1).score > game.getParty(team2).score }
            .count()
    return wins * 100 / games.size
}