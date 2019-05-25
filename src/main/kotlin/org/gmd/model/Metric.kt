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


}