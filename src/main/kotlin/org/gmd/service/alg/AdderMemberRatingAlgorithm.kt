package org.gmd.service.alg

import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.Score
import org.springframework.stereotype.Component

@Component
open class AdderMemberRatingAlgorithm : MemberRatingAlgorithm {

    override fun rate(games: List<Game>): List<Score> {
        val scores: List<Pair<String, Int>> = games
                .flatMap { game -> game.parties }
                .flatMap {
                    party ->
                    party.members.map {
                        member ->
                        member.name to party.score
                    }
                }
        return scores.groupBy({ it.first }, { it.second })
                .mapValues { (_, v) -> v.sum() }
                .map { (k, v) -> Score(k, v) }
    }

    override fun evolution(games: List<Game>): List<Evolution> {
        val ratedPlayers = mutableMapOf<String, List<Int>>()
        ratePlayersInGame(ratedPlayers, games.sortedBy { game -> game.timestamp })
        return ratedPlayers.map {
            rating ->
            Evolution(rating.key, rating.value)
        }
    }

    class RatedMember(val name: String, val score: Int, val rating: List<Int>)

    private tailrec fun ratePlayersInGame(ratings: MutableMap<String, List<Int>>, games: List<Game>): MutableMap<String, List<Int>> {
        return when {
            games.isNotEmpty() -> {
                val currentStatus = games.first().parties.flatMap { party ->
                    party.members.map {
                        member ->
                        RatedMember(member.name, party.score, ratings.getOrDefault(member.name, listOf(0)))
                    }
                }

                currentStatus.forEach {
                    member ->
                    run {
                        val newRating = member.rating.last() + member.score
                        ratings.put(member.name, member.rating + newRating)
                    }
                }

                return ratePlayersInGame(ratings, games.drop(1))
            }
            else -> ratings
        }

    }

}
