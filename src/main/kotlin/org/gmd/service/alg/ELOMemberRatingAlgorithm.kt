package org.gmd.service.alg

import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.Score
import org.springframework.stereotype.Component

@Component
open class ELOMemberRatingAlgorithm: MemberRatingAlgorithm {
    
    override fun rate(games: List<Game>): List<Score> {
        val ratedPlayers = mutableMapOf<String, List<Pair<Double, Long>>>()
        ratePlayersInGame(ratedPlayers, games.filter { game -> game.parties.size > 1 }.sortedBy { game -> game.timestamp })
        return ratedPlayers.map {
            rating ->
            Score(rating.key, Math.round(rating.value.last().first).toInt(), rating.value.size - 1)
        }
    }

    override fun evolution(games: List<Game>): List<Evolution> {
        val ratedPlayers = mutableMapOf<String, List<Pair<Double, Long>>>()
        ratePlayersInGame(ratedPlayers, games.filter { game -> game.parties.size > 1 }.sortedBy { game -> game.timestamp })
        return ratedPlayers.map {
            rating ->
            Evolution(rating.key, rating.value.map { v -> Pair(Math.round(v.first).toInt(), v.second)  } )
        }
    }


    class RatedMember(val name: String, val score: Int, val rating: List<Pair<Double, Long>>)

    private tailrec fun ratePlayersInGame(ratings: MutableMap<String, List<Pair<Double, Long>>>, games: List<Game>): MutableMap<String, List<Pair<Double, Long>>> {
        return when {
            games.isNotEmpty() -> {
                val firstGame = games.first()
                val currentStatus = firstGame.parties.flatMap { party ->
                    party.members.map {
                        member ->
                        RatedMember(member.name, party.score, ratings.getOrDefault(member.name, listOf(Pair(1200.0, 0L))))
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

                        val newRating = member.rating.last().first + differentScoreDeltas.sum() / differentScoreDeltas.size
                        ratings.put(member.name, member.rating + Pair(newRating, firstGame.timestamp!!))
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
        return 1.0 * 1.0 / (1 + 1.0 *
                Math.pow(10.0, 1.0 * (a.rating.last().first - b.rating.last().first) / 400))
    }
}