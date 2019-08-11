package org.gmd.service.alg

import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.Score
import org.springframework.stereotype.Component

@Component
open class ELOMemberRatingAlgorithm: MemberRatingAlgorithm {
    
    override fun rate(games: List<Game>): List<Score> {
        val ratedPlayers = mutableMapOf<String, List<Double>>()
        ratePlayersInGame(ratedPlayers, games.filter { game -> game.parties.size > 1 }.sortedBy { game -> game.timestamp })
        return ratedPlayers.map {
            rating ->
            Score(rating.key, Math.round(rating.value.last()).toInt(), rating.value.size - 1)
        }
    }

    override fun evolution(games: List<Game>): List<Evolution> {
        val ratedPlayers = mutableMapOf<String, List<Double>>()
        ratePlayersInGame(ratedPlayers, games.filter { game -> game.parties.size > 1 }.sortedBy { game -> game.timestamp })
        return ratedPlayers.map {
            rating ->
            Evolution(rating.key, rating.value.map { v -> Math.round(v).toInt()  } )
        }
    }


    class RatedMember(val name: String, val score: Int, val rating: List<Double>)

    private tailrec fun ratePlayersInGame(ratings: MutableMap<String, List<Double>>, games: List<Game>): MutableMap<String, List<Double>> {
        return when {
            games.isNotEmpty() -> {
                val currentStatus = games.first().parties.flatMap { party ->
                    party.members.map {
                        member ->
                        RatedMember(member.name, party.score, ratings.getOrDefault(member.name, listOf(1200.0)))
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

                        val newRating = member.rating.last() + differentScoreDeltas.sum() / differentScoreDeltas.size
                        ratings.put(member.name, member.rating + newRating)
                    }
                }

                return ratePlayersInGame(ratings, games.drop(1))
            }
            else -> ratings
        }

    }

    private fun eloRatingDeltaForA(a: RatedMember, b: RatedMember, k: Int = 30): Double {
        return if (a.score > b.score) {
            k * probabilityOfWinForB(a, b)
        } else {
            k * (probabilityOfWinForB(a, b) - 1)
        }
    }

    private fun probabilityOfWinForB(a: RatedMember, b: RatedMember): Double {
        val ratingA = a.rating.last()
        val ratingB = b.rating.last()
        return probabilityOfWinForBRating(ratingA, ratingB)
    }

}


fun probabilityOfWinForBRating(ratingA: Double, ratingB: Double): Double {
    return 1.0 * 1.0 / (1 + 1.0 *
            Math.pow(10.0, 1.0 * (ratingA - ratingB) / 400))
}