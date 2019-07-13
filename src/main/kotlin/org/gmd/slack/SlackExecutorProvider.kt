package org.gmd.slack

interface SlackExecutorProvider {
    fun asyncResponseExecutorFor(responseUrl: String): (SlackResponse) -> Unit
    
    fun oauthExecutor(oauthUrl: String = "https://slack.com/api/oauth.access"): 
            (clientId: String, clientSecret: String, code: String) -> SlackTeamAuth
}