package org.gmd.slack

interface SlackAsyncExecutorProvider {
    fun executorFor(responseUrl: String): (SlackResponse) -> Unit
}