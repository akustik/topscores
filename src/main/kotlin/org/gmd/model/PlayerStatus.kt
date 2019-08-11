package org.gmd.model

class PlayerStatus() {
    lateinit var evolution: List<Int>
    lateinit var metrics: List<MemberMetrics>

    constructor(
            evolution: Evolution,
            metrics: List<MemberMetrics>): this() {
        this.evolution = evolution.score.map { it.first }
        this.metrics = metrics
    }
}