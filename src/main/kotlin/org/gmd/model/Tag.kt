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


}