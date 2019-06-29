package org.gmd.model

import io.swagger.annotations.ApiModelProperty

class Tag() {
    @ApiModelProperty(notes = "Use format name:<player_name> to indicate an individual tag.")
    lateinit var name: String
    lateinit var value: String
    
    constructor(
            name: String,
            value: String): this() {
        this.name = name
        this.value = value
    }

    override fun toString(): String {
        return "Tag(name='$name', value='$value')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tag

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}