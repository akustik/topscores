package org.gmd.form


class SimpleTeam() {
    lateinit var team: String
    lateinit var players: List<String>
    var metrics: List<SimpleMetric> = listOf()
    var score: Int = 0
    
    constructor(
            team: String,
            players: List<String>,
            score: Int
    ) : this() {
        this.team = team
        this.players = players
        this.score = score
    }
}
