package org.gmd.slack

class SlackExecutorProviderForTesting : SlackExecutorProvider {
    val accumulatedResponses = HashMap<String, SlackResponse>()

    override fun asyncResponseExecutorFor(responseUrl: String): (SlackResponse) -> Unit = { response ->
        run {
            accumulatedResponses.put(responseUrl, response)
        }
    }

    override fun oauthExecutor(oauthUrl: String): (clientId: String, clientSecret: String, code: String) -> SlackTeamAuth {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}