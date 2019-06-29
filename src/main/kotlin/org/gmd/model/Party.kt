package org.gmd.model

import io.swagger.annotations.ApiModelProperty


class Party() {
    lateinit var team: Team
    lateinit var members: List<TeamMember>
    var metrics: List<Metric> = listOf()
    var tags: List<Tag> = listOf()

    @ApiModelProperty(notes = "The score of the team for this game. The higher the better. Zero means no scoring.")
    var score: Int = 1

    constructor(
            team: Team,
            members: List<TeamMember>,
            metrics: List<Metric>,
            tags: List<Tag>,
            score: Int
            ): this() {
        this.team = team
        this.members = members
        this.metrics = metrics
        this.tags = tags
        this.score = score
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Party

        if (team != other.team) return false
        if (members != other.members) return false
        if (metrics != other.metrics) return false
        if (tags != other.tags) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = team.hashCode()
        result = 31 * result + members.hashCode()
        result = 31 * result + metrics.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + score
        return result
    }
}
