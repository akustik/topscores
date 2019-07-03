package org.gmd.model

class TeamMember() {
    lateinit var name: String

    constructor(
            name: String): this() {
        this.name = name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeamMember

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "TeamMember(name='$name')"
    }
}