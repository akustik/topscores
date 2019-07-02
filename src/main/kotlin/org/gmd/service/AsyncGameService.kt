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
            consumer: (List<Score>) -> Unit): Unit

    fun consumeTournamentMemberScoreEvolution(account: String,
                                              tournament: String,
                                              player: List<String>,
                                              alg: Algorithm = Algorithm.SUM,
                                              withGames: List<Game> = emptyList(),
                                              consumer: (List<Evolution>) -> Unit): Unit
}