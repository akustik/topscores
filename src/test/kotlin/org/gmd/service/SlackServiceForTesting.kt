package org.gmd.service

import org.gmd.slack.SlackTeamAuth

class SlackServiceForTesting: SlackService {
    
    override fun oauth(code: String): SlackTeamAuth {
        TODO("not implemented") 
    }
}