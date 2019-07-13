package org.gmd.repository

import org.gmd.slack.SlackTeamAuth

interface SlackRepository {

    fun storeAuth(auth: SlackTeamAuth)

    fun getAuth(teamName: String): SlackTeamAuth

}