package org.gmd.model

import io.swagger.annotations.ApiModelProperty

class Metric() {
    @ApiModelProperty(notes = "Use format name:<player_name> to indicate an individual metric.")
    lateinit var name: String
    var value: Int = 0
    
    constructor(
            name: String,
            value: Int): this() {
        this.name = name
        this.value = value
    }

    override fun toString(): String {
        return "Metric(name='$name', value=$value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Metric

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value
        return result
    }


}