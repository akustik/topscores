package org.gmd.slack.command

import org.gmd.Algorithm

interface SlackCommand {

    fun normalizePlayers(players: List<String>): List<List<String>> {
        val argString = players
                .joinToString(" ").toLowerCase()
        val teamString = argString.split(",", ".", "|")
        return if(teamString.size == 1) {
            teamString.first().split(" ").map { t -> listOf(t) }
        } else {
            teamString.map { t -> t.trim().split(" ") }
        }
    }

    fun parseAlgorithm(alg: String): Algorithm {
        return Algorithm.valueOf(alg.toUpperCase())
    }
}
