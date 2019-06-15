package org.gmd.service.alg

import org.gmd.model.Evolution
import org.gmd.model.Game
import org.gmd.model.Score

interface MemberRatingAlgorithm {

    fun rate(games: List<Game>): List<Score>

    fun evolution(games: List<Game>): List<Evolution>

}