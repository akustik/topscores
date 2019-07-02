package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.Score
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
open class AsyncGameServiceWrapper(val service: GameService) : AsyncGameService {

    @Async
    override fun consumeTournamentMemberScores(
            account: String,
            tournament: String,
            alg: Algorithm,
            consumer: (List<Score>) -> Unit
    ): Unit {
        consumer(service.computeTournamentMemberScores(
                account = account,
                tournament = tournament,
                alg = alg)
        )
    }

    @Async
    override fun consumeTournamentMemberScoreEvolution(account: String,
                                                       tournament: String,
                                                       player: List<String>,
                                                       alg: Algorithm,
                                                       withGames: List<Game>,
                                                       consumer: (List<Evolution>) -> Unit): Unit {
        consumer(service.computeTournamentMemberScoreEvolution(
                account = account,
                tournament = tournament,
                player = player,
                withGames = withGames,
                alg = alg)
        )

    }

}