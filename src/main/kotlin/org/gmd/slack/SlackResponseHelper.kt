package org.gmd.slack


class SlackResponseHelper(val asyncExecutor: (SlackResponse) -> Unit) {

    var slackResponse: SlackResponse? = SlackResponse()

    fun internalMessage(text: String) {
        message(text = text, silent = true)
    }

    fun publicMessage(text: String, attachments: List<String> = emptyList()) {
        message(text = text, attachments = attachments, silent = false)
    }

    fun message(text: String, attachments: List<String> = emptyList(), silent: Boolean = true) {
        slackResponse = responseOf(text, attachments, silent)
    }

    fun asJson(): String? {
        return slackResponse?.asJson()
    }

    fun asyncDefaultResponse() {
        internalMessage("ACK! You will get the response in a few seconds.")
    }

    fun emptyResponse() {
        slackResponse = null
    }

    fun asyncMessage(text: String, attachments: List<String> = emptyList(), silent: Boolean = true) {
        asyncExecutor(responseOf(text, attachments, silent))
    }
    
    fun currentResponseAsyncMessage() {
        asyncExecutor(slackResponse!!)
    }

    private fun responseOf(text: String, attachments: List<String>, silent: Boolean): SlackResponse {
        return SlackResponse(
                text = text,
                attachments = attachments.map { a -> SlackAttachment(a) },
                responseType = if (!silent) SlackResponse.IN_CHANNEL else SlackResponse.EPHEMERAL
        )
    }

}