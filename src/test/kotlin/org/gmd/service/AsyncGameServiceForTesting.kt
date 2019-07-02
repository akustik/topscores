package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.Score

open class AsyncGameServiceForTesting(val service: GameService) : AsyncGameService {
    override fun consumeTournamentMemberScoreEvolution(account: String, tournament: String, player: List<String>, alg: Algorithm, withGames: List<Game>, consumer: (List<Evolution>) -> Unit) {
        consumer(service.computeTournamentMemberScoreEvolution(account, tournament, player, alg, withGames))
    }

    override fun consumeTournamentMemberScores(account: String, tournament: String, alg: Algorithm, consumer: (List<Score>) -> Unit) {
        consumer(service.computeTournamentMemberScores(account, tournament, alg))
    }
}

