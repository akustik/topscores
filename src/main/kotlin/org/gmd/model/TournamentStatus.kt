package org.gmd.model

class TournamentStatus() {
    lateinit var scores: List<Score>
    lateinit var metrics: List<TournamentMetrics>
    lateinit var availableMetrics: List<String>

    constructor(
            scores: List<Score>,
            metrics: List<TournamentMetrics>,
            availableMetrics: List<String>): this() {
        this.scores = scores
        this.metrics = metrics
        this.availableMetrics = availableMetrics
    }
}