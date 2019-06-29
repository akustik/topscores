package org.gmd.model

class Score(val member: String, val score: Int) {

    override fun toString(): String {
        return "Score(member='$member', score=$score)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Score

        if (member != other.member) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = member.hashCode()
        result = 31 * result + score
        return result
    }
}