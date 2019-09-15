package org.gmd

import org.gmd.model.Game
import org.gmd.model.Party
import org.gmd.model.Team
import org.gmd.model.TeamMember
import java.nio.charset.Charset

class TestData {

    companion object {
        val patxanga = TestData::class.java.getResource("/samples/patxanga.json").readText()
        val patxanga_tie = TestData::class.java.getResource("/samples/patxanga_tie.json").readText()
        val patxanga_no_timestamp = TestData::class.java.getResource("/samples/patxanga_no_timestamp.json").readText()
        val mariokart = TestData::class.java.getResource("/samples/mariokart.json").readText()

        fun patxanga(): Game {
            return parse(patxanga)
        }

        fun patxangaTie(): Game {
            return parse(patxanga_tie)
        }

        fun mariokart(): Game {
            return parse(mariokart)
        }

        const val player1 = "player1"
        const val player2 = "player2"
        val tournament = "patxanga"
        val timestamp = 12345L

        fun dummy(): Game {
            return Game(
                    tournament = tournament,
                    parties = listOf(
                            Party(
                                    team = Team(player2),
                                    members = listOf(TeamMember(player2)),
                                    metrics = listOf(),
                                    tags = listOf(),
                                    score = 1
                            ),
                            Party(
                                    team = Team(player1),
                                    members = listOf(TeamMember(player1)),
                                    metrics = listOf(),
                                    tags = listOf(),
                                    score = 2
                            )
                    ),
                    timestamp = timestamp)
        }

        private fun parse(data: String): Game {
            return Game.fromJsonBytes(data.toByteArray(Charset.defaultCharset()))
        }
    }
}
