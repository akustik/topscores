package org.gmd.slack.executor

import org.gmd.slack.model.SlackResponse
import org.gmd.slack.model.SlackTeamAuth

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

    override fun webApiExecutor(url: String): (method: String, jsonBody: String, accessToken: String) -> String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun webApiPaginatedExecutor(url: String): (method: String, accessToken: String) -> List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}