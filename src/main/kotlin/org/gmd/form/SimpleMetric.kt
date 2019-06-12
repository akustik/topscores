package org.gmd.form


class SimpleMetric() {
    lateinit var players: List<String>
    lateinit var metric: String
    var value: Int = 0
    
    constructor(
            metric: String,
            players: List<String>,
            value: Int
    ) : this() {
        this.metric = metric
        this.players = players
        this.value = value
    }
}
