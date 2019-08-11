package org.gmd.service.alg

import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.Score
import org.springframework.stereotype.Component

@Component
open class AdderMemberRatingAlgorithm : MemberRatingAlgorithm {

    override fun rate(games: List<Game>): List<Score> {
        val ratedPlayers = mutableMapOf<String, List<Pair<Int, Long>>>()
        ratePlayersInGame(ratedPlayers, games.sortedBy { game -> game.timestamp })
        return ratedPlayers.map {
            rating ->
            Score(rating.key, rating.value.last().first, rating.value.size - 1)
        }
    }

    override fun evolution(games: List<Game>): List<Evolution> {
        val ratedPlayers = mutableMapOf<String, List<Pair<Int, Long>>>()
        ratePlayersInGame(ratedPlayers, games.sortedBy { game -> game.timestamp })
        return ratedPlayers.map {
            rating ->
            Evolution(rating.key, rating.value)
        }
    }

    class RatedMember(val name: String, val score: Int, val rating: List<Pair<Int, Long>>)

    private tailrec fun ratePlayersInGame(ratings: MutableMap<String, List<Pair<Int, Long>>>, games: List<Game>): MutableMap<String, List<Pair<Int, Long>>> {
        return when {
            games.isNotEmpty() -> {
                val firstGame = games.first()
                val currentStatus = firstGame.parties.flatMap { party ->
                    party.members.map {
                        member ->
                        RatedMember(member.name, party.score, ratings.getOrDefault(member.name, listOf(Pair(0, 0L))))
                    }
                }

                currentStatus.forEach {
                    member ->
                    run {
                        val newRating = member.rating.last().first + member.score
                        ratings.put(member.name, member.rating + Pair(newRating, firstGame.timestamp!!))
                    }
                }

                return ratePlayersInGame(ratings, games.drop(1))
            }
            else -> ratings
        }

    }

}
