package org.gmd.slack.command

import org.gmd.Algorithm

interface SlackCommand {

    fun normalizePlayers(players: List<String>): List<List<String>> {
        return players.map { it.toLowerCase().split("_") }
    }

    fun parseAlgorithm(alg: String): Algorithm {
        return Algorithm.valueOf(alg.toUpperCase())
    }
}
