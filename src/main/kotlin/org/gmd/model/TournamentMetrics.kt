package org.gmd.model

class TournamentMetrics() {
    lateinit var member: String
    lateinit var metrics: Map<String, Int>

    constructor(
            member: String,
            metrics: Map<String, Int>): this() {
        this.member = member
        this.metrics = metrics
    }
}