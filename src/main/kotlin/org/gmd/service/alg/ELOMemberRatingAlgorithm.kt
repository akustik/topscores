package org.gmd.service.alg

import org.gmd.model.Game
import org.gmd.model.Score
import org.springframework.stereotype.Component

@Component
open class ELOMemberRatingAlgorithm: MemberRatingAlgorithm {
    
    override fun rate(games: List<Game>): List<Score> {
        val ratedPlayers = mutableMapOf<String, Double>()
        ratePlayersInGame(ratedPlayers, games.filter { game -> game.parties.size > 1 }.sortedBy { game -> game.timestamp })
        return ratedPlayers.map {
            rating ->
            Score(rating.key, Math.round(rating.value).toInt())
        }
    }

    class RatedMember(val name: String, val score: Int, val rating: Double)

    tailrec private fun ratePlayersInGame(ratings: MutableMap<String, Double>, games: List<Game>): MutableMap<String, Double> {
        return when {
            games.isNotEmpty() -> {
                val currentStatus = games.first().parties.flatMap { party ->
                    party.members.map {
                        member ->
                        RatedMember(member.name, party.score, ratings.getOrDefault(member.name, 1200.0))
                    }
                }

                currentStatus.forEach {
                    member ->
                    run {
                        val differentScoreDeltas = currentStatus.filter {
                            m ->
                            m.score != member.score
                        }.map {
                            m ->
                            eloRatingDeltaForA(member, m)
                        }

                        val newRating = member.rating + differentScoreDeltas.sum() / differentScoreDeltas.size
                        ratings.put(member.name, newRating)
                    }
                }

                return ratePlayersInGame(ratings, games.drop(1))
            }
            else -> ratings
        }

    }

    private fun eloRatingDeltaForA(a: RatedMember, b: RatedMember, k: Int = 30): Double {
        if (a.score > b.score) {
            return k * probabilityOfWinForB(a, b)
        } else {
            return k * (probabilityOfWinForB(a, b) - 1)
        }
    }

    private fun probabilityOfWinForB(a: RatedMember, b: RatedMember): Double {
        return 1.0 * 1.0 / (1 + 1.0 *
                Math.pow(10.0, 1.0 * (a.rating - b.rating) / 400))
    }
}