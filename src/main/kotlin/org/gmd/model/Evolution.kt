package org.gmd.model

class Evolution(val member: String, val score: List<Pair<Int, Long>>) {

    override fun toString(): String {
        return "Evolution(member='$member', score=$score)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Evolution

        if (member != other.member) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = member.hashCode()
        result = 31 * result + score.hashCode()
        return result
    }

    companion object {
        private fun variationToString(value: Int): String {
            return if (value > 0) "+$value" else "$value"
        }

        fun computeRatingChangesForGames(evolution: List<Evolution>, numberOfGames: Int = 1): String {
            val eloUpdate = evolution
                    .map { e -> Triple(e.member, e.score.last().first, e.score.last().first - e.score.dropLast(numberOfGames).last().first) }
                    .filter { t -> t.third != 0 }
                    .sortedByDescending { p -> p.third }

            return eloUpdate.mapIndexed { index, s -> "${index + 1}. ${s.first} (${s.second}, ${variationToString(s.third)})" }.joinToString(separator = "\n")
        }

        fun computeRatingChangesForTime(evolution: List<Evolution>, minTimestamp: Long = 0L): String {
            val eloUpdate = evolution
                    .map { e -> Triple(e.member, e.score.last().first, e.score.last().first - e.score.dropLastWhile { p -> p.second > minTimestamp }.last().first) }
                    .filter { t -> t.third != 0 }
                    .sortedByDescending { p -> p.third }

            return eloUpdate.mapIndexed { index, s -> "${index + 1}. ${s.first} (${s.second}, ${variationToString(s.third)})" }.joinToString(separator = "\n")
        }
        
        fun computePlayerEvolution(evolution: Evolution): String {
            return evolution.score.mapIndexed { index, score -> "${index + 1}. $score" }
                    .joinToString(separator = "\n")
        }
    }
}