package com.example.model


class Game() {
    lateinit var account: String
    lateinit var parties: List<Party>
    var timestamp: Long = System.currentTimeMillis()

    constructor(
            account: String,
            parties: List<Party>,
            timestamp: Long): this() {
        this.account = account
        this.parties = parties
        this.timestamp = timestamp
    }
}
