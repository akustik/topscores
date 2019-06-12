package org.gmd.form


class SimpleGame() {
    lateinit var tournament: String
    lateinit var teams: List<SimpleTeam>

    constructor(
            tournament: String,
            parties: List<SimpleTeam>) : this() {
        this.tournament = tournament
        this.teams = parties
    }
}
