package org.gmd.slack

interface SlackExecutorProvider {
    fun asyncResponseExecutorFor(responseUrl: String): (SlackResponse) -> Unit
    
    fun oauthExecutor(oauthUrl: String = "https://slack.com/api/oauth.access"): 
            (clientId: String, clientSecret: String, code: String) -> SlackTeamAuth
    
    fun webApiExecutor(url: String = "https://slack.com/api/"):
            (method: String, jsonBody: String, accessToken: String) -> String
}