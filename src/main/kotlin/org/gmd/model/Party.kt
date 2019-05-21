package org.gmd.model


class Party() {
    lateinit var team: Team
    lateinit var members: List<TeamMember>
    lateinit var metrics: List<Metric>
    var score: Int = 1

    constructor(
            team: Team,
            members: List<TeamMember>,
            metrics: List<Metric>,
            score: Int
            ): this() {
        this.team = team
        this.members = members
        this.metrics = metrics
        this.score = score
    }
}
