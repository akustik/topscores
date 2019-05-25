package org.gmd.model

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.ApiModelProperty


class Game() {
    @ApiModelProperty(notes = "Tournaments are used to have different scores and metrics per player.")
    lateinit var tournament: String
    lateinit var parties: List<Party>

    @ApiModelProperty(notes = "If not set, the system is going to automatically set it with the current time.")
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
