package com.example.model


class Game() {
    lateinit var account: String
    lateinit var parties: List<Party>

    constructor(
            account: String,
            parties: List<Party>): this() {
        this.account = account
        this.parties = parties
    }
}
