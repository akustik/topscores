package org.gmd.slack.model

import com.fasterxml.jackson.annotation.JsonProperty

class SlackDialogSelectUserElement() {

    lateinit var label: String
    lateinit var name: String
    var type: String = "select"
    
    @JsonProperty("data_source")
    var dataSource: String = "users"

    constructor(label: String, name: String) : this() {
        this.label = label
        this.name = name
    }
}

