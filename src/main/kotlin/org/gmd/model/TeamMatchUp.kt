package org.gmd.model

class TeamMatchUp(val team1: Team, val team2: Team, val games: List<Game>) {

    fun calculateWinRatio(): Int {
        val wins = games.filter { game -> game.getParty(team1).score < game.getParty(team2).score }
                .count()
        return wins * 100 / games.size
    }

}