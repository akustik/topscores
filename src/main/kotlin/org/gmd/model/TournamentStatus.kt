package org.gmd.model

class TournamentStatus() {
    lateinit var scores: List<Score>
    lateinit var metrics: List<MemberMetrics>

    constructor(
            scores: List<Score>,
            metrics: List<MemberMetrics>): this() {
        this.scores = scores
        this.metrics = metrics
    }
}