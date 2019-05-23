package org.gmd.service.alg

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
}
