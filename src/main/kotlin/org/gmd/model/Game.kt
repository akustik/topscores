package org.gmd.model

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.ApiModelProperty
import org.gmd.EnvProvider
import org.gmd.form.SimpleGame


class Game() {
    @ApiModelProperty(notes = "Tournaments are used to have different scores and metrics per player.")
    lateinit var tournament: String
    lateinit var parties: List<Party>

    @ApiModelProperty(notes = "If not set, the system is going to automatically set it with the current time.")
    var timestamp: Long? = null

    constructor(
            tournament: String,
            parties: List<Party>,
            timestamp: Long? = null) : this() {
        this.tournament = tournament
        this.parties = parties
        this.timestamp = timestamp
    }

    fun getParty(team: Team): Party {
        return parties.first { party -> party.team == team }
    }

    fun contains(team: Team): Boolean {
        return parties.any { party -> party.team == team }
    }

    fun partiesDescendingByScore(): List<Party> = parties.sortedByDescending { p -> p.score }

    fun toJsonBytes(): ByteArray {
        return ObjectMapper().writeValueAsBytes(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (tournament != other.tournament) return false
        if (parties != other.parties) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tournament.hashCode()
        result = 31 * result + parties.hashCode()
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Game(tournament='$tournament', parties=$parties, timestamp=$timestamp)"
    }


    companion object {
        fun fromJsonBytes(bytes: ByteArray): Game {
            return ObjectMapper().readValue(bytes, Game::class.java)
        }

        fun playerOrderedListToGame(tournament: String, players: List<String>): Game {
            val parties = players.reversed().mapIndexed { index, player ->
                Party(
                        team = Team(player),
                        members = listOf(TeamMember(player)),
                        score = index + 1,
                        metrics = emptyList(),
                        tags = emptyList()
                )
            }

            return Game(
                    tournament = tournament,
                    parties = parties,
                    timestamp = null
            )
        }
        
        fun simpleGame(game: SimpleGame): Game {
            val parties = game.teams.map { t ->
                Party(
                        team = Team(name = t.team),
                        members = t.players.map { player -> TeamMember(name = player) },
                        score = t.score,
                        metrics = t.metrics.flatMap { metric -> metric.players.map { player -> Metric("${metric.metric}:${player}", metric.value) } },
                        tags = emptyList()
                )
            }
            return Game(
                    tournament = game.tournament,
                    parties = parties,
                    timestamp = null
            )
        }

        fun withCollectionTimeIfTimestampIsNotPresent(env: EnvProvider, game: Game): Game {
            game.timestamp = game.timestamp?.let { game.timestamp } ?: env.getCurrentTimeInMillis()
            return game
        }

        fun withTimestamp(timestamp: Long, game: Game): Game {
            game.timestamp = timestamp
            return game
        }
    }
}
