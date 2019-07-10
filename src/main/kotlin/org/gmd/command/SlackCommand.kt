package org.gmd.command

import org.gmd.Algorithm

interface SlackCommand {
    
    fun normalizePlayers(players: List<String>): List<String> {
        return players.map { it.toLowerCase() }
    }
    
    fun parseAlgorithm(alg: String): Algorithm {
        return Algorithm.valueOf(alg.toUpperCase())
    }
}