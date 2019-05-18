package com.example.model


class Party() {
    lateinit var team: Team
    lateinit var members: List<TeamMember>
    lateinit var metrics: List<Metric>
    var position: Int = 1

    constructor(
            team: Team,
            members: List<TeamMember>,
            metrics: List<Metric>,
            position: Int
            ): this() {
        this.team = team
        this.members = members
        this.metrics = metrics
        this.position = position
    }
}
