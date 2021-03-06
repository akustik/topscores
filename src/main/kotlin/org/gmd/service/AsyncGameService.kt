package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.Score

interface AsyncGameService {

    fun consumeTournamentMemberScores(
            account: String,
            tournament: String,
            alg: Algorithm = Algorithm.SUM,
            teams: List<String> = emptyList(),
            consumer: (List<Score>) -> Unit)

    fun consumeTournamentMemberScoreEvolution(account: String,
                                              tournament: String,
                                              player: List<String> = emptyList(),
                                              alg: Algorithm = Algorithm.SUM,
                                              withGames: List<Game> = emptyList(),
                                              consumer: (List<Evolution>) -> Unit)
}