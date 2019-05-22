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
    
    override fun listGames(account: String): List<Game> = repository.listGames(account)

    override fun addGame(account: String, game: Game): Game = repository.addGame(account, game)

    override fun computeTournamentScores(account: String, tournament: String): List<Score> {
        val scores: List<Pair<String, Int>> = repository.listGames(account = account, tournament = tournament)
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