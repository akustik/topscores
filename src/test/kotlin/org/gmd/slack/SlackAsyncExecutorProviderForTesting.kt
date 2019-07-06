package org.gmd.slack

class SlackAsyncExecutorProviderForTesting : SlackAsyncExecutorProvider {

    val accumulatedResponses = HashMap<String, SlackResponse>()

    override fun executorFor(responseUrl: String): (SlackResponse) -> Unit = { response ->
        run {
            accumulatedResponses.put(responseUrl, response)
        }
    }

}