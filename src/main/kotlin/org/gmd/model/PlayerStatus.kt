package org.gmd.model

class PlayerStatus() {
    lateinit var evolution: Evolution
    lateinit var metrics: List<MemberMetrics>

    constructor(
            evolution: Evolution,
            metrics: List<MemberMetrics>): this() {
        this.evolution = evolution
        this.metrics = metrics
    }
}