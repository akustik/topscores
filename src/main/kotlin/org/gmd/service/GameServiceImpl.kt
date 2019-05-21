package org.gmd.service

import org.gmd.model.Game
import org.gmd.model.Score
import org.gmd.repository.jdbc.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.SortedSet

@Component
open class GameServiceImpl: GameService {

    //FIXME: Use private
    @Autowired
    lateinit var repository: GameRepository
    
    override fun listGames(): List<Game> = repository.listGames()

    override fun addGame(game: Game): Game = repository.addGame(game)

    override fun getAccountScores(account: String): List<Score> {
        val scores: List<Pair<String, Int>> = repository.listGames(account)
                .flatMap { game -> game.parties }
                .flatMap {
                    party ->
                    party.members.map {
                        member ->
                        member.name to party.score
                    }
                }
        val sortedScores: SortedSet<Score> = scores.groupBy({ it.first }, { it.second })
                .mapValues { (k, v) -> v.sum() }
                .map { (k, v) -> Score(k, v) }
                .toSortedSet(compareBy({ score -> score.score }, { score -> score.member }))

        return sortedScores.reversed()
    }
}