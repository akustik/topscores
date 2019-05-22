package org.gmd.model

import com.fasterxml.jackson.databind.ObjectMapper


class Game() {
    lateinit var tournament: String
    lateinit var parties: List<Party>
    var timestamp: Long? = null

    constructor(
            tournament: String,
            parties: List<Party>,
            timestamp: Long): this() {
        this.tournament = tournament
        this.parties = parties
        this.timestamp = timestamp
    }
    
    fun toJsonBytes(): ByteArray {
        return ObjectMapper().writeValueAsBytes(this)
    }

    companion object {
        fun fromJsonBytes(bytes : ByteArray): Game {
            return ObjectMapper().readValue(bytes, Game::class.java)
        }
    }
}
