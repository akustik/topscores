package org.gmd.model

class MemberMetrics(val member: String, val metrics: List<Metric>) {
    
    override fun toString(): String {
        return "MemberMetrics(member='$member', metrics=$metrics)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MemberMetrics

        if (member != other.member) return false
        if (metrics != other.metrics) return false

        return true
    }

    override fun hashCode(): Int {
        var result = member.hashCode()
        result = 31 * result + metrics.hashCode()
        return result
    }
}