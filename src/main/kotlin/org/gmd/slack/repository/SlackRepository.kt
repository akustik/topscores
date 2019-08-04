package org.gmd.slack.repository

import org.gmd.slack.model.SlackTeamAuth

interface SlackRepository {

    fun storeAuth(auth: SlackTeamAuth)

    fun getAuth(teamName: String): SlackTeamAuth

}