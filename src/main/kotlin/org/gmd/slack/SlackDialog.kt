package org.gmd.slack

import com.fasterxml.jackson.annotation.JsonProperty

class SlackDialog() {

    @JsonProperty("callback_id")
    lateinit var callbackId: String

    lateinit var title: String
    lateinit var elements: List<SlackDialogSelectUserElement>

    constructor(callbackId: String, title: String, elements: List<SlackDialogSelectUserElement>) : this() {
        this.callbackId = callbackId
        this.title = title
        this.elements = elements
    }
}

