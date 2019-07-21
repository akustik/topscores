package org.gmd.slack

import com.fasterxml.jackson.annotation.JsonProperty

class SlackWebApiResponseMetadata() {

    @JsonProperty("next_cursor")
    var nextCursor: String? = null
    
    constructor(nextCursor: String) : this() {
        this.nextCursor = nextCursor
    }

    override fun toString(): String {
        return "SlackWebApiResponseMetadata(nextCursor=$nextCursor)"
    }


}

