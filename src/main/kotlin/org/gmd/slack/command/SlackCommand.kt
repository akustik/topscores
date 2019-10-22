package org.gmd.slack.command

import org.gmd.Algorithm

interface SlackCommand {

    fun normalizePlayers(players: List<String>): List<List<String>> {
        return partition(players, emptyList())
    }

    private tailrec fun partition(players: List<String>, acc: List<List<String>>): List<List<String>> {
        return if (players.isEmpty()) {
            acc
        } else {
            val idx = idxForSeparator(players)
            if (idx >= 0) {
                val remaining = players.subList(idx + 1, players.size)
                val team = players.subList(0, idx)
                partition(remaining, acc + listOf(team))
            } else if (acc.isEmpty()) {
                partition(emptyList(), players.map { p -> listOf(p) })
            } else {
                partition(emptyList(), acc + listOf(players))
            }
        }
    }

    private fun idxForSeparator(players: List<String>): Int {
        return maxOf(players.indexOf(","), players.indexOf("."), players.indexOf("|"))
    }


    fun parseAlgorithm(alg: String): Algorithm {
        return Algorithm.valueOf(alg.toUpperCase())
    }
}
