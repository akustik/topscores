package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.Game
import org.gmd.model.Score
import org.gmd.repository.GameRepository
import org.gmd.service.alg.AdderMemberRatingAlgorithm
import org.gmd.service.alg.ELOMemberRatingAlgorithm
import org.springframework.stereotype.Component

@Component
open class GameServiceImpl(val repository: GameRepository, 
                           val adderAlg: AdderMemberRatingAlgorithm,
                           val eloAlg: ELOMemberRatingAlgorithm) : GameService {
    
    override fun listGames(account: String): List<Game> = repository.listGames(account)

    override fun addGame(account: String, game: Game): Game = repository.addGame(account, game)

    override fun computeTournamentScores(account: String, tournament: String, alg: Algorithm): List<Score> {
        val games = repository.listGames(account = account, tournament = tournament)
        return when (alg) {
            Algorithm.SUM -> descendent(adderAlg.rate(games))
            Algorithm.ELO -> descendent(eloAlg.rate(games))
            else -> adderAlg.rate(games)
        }
    }

    private fun descendent(scores: List<Score>): List<Score> {
        return scores.toSortedSet(compareBy({ score -> score.score }, { score -> score.member })).reversed()
    }
}