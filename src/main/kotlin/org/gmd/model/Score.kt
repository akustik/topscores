package org.gmd.model

class Score(val member: String, val score: Int, val games: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Score

        if (member != other.member) return false
        if (score != other.score) return false
        if (games != other.games) return false

        return true
    }

    override fun hashCode(): Int {
        var result = member.hashCode()
        result = 31 * result + score
        result = 31 * result + games
        return result
    }

    override fun toString(): String {
        return "Score(member='$member', score=$score, games=$games)"
    }

    companion object {
        fun computeLeaderboard(scores: List<Score>, minGames: Int): String {
            return scores
                    .filter { s -> s.games >= minGames }
                    .mapIndexed { index, score -> "${index + 1}. ${score.member} (${score.score})" }
                    .joinToString(separator = "\n")
        }
    }
}