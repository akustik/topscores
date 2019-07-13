package org.gmd.service

import org.gmd.EnvProvider
import org.gmd.repository.SlackRepository
import org.gmd.slack.SlackExecutorProvider
import org.gmd.slack.SlackTeamAuth
import org.springframework.stereotype.Component

@Component
class SlackServiceImpl(private val env: EnvProvider, 
                       private val slackExecutorProvider: SlackExecutorProvider, 
                       private val slackRepository: SlackRepository) : SlackService {

    override fun oauth(code: String): SlackTeamAuth {
        val slackSecret = env.getEnv()[EnvProvider.SLACK_SECRET]
        val slackClientId = env.getEnv()[EnvProvider.SLACK_CLIENT_ID]
        val response = slackExecutorProvider.oauthExecutor()(slackClientId!!, slackSecret!!, code)
        slackRepository.storeAuth(response)
        return response
    }

}