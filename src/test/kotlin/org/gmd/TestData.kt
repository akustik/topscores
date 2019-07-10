package org.gmd

import org.gmd.model.Game
import java.nio.charset.Charset

class TestData {

    companion object {
        val patxanga = TestData::class.java.getResource("/samples/patxanga.json").readText()
        val patxanga_no_timestamp = TestData::class.java.getResource("/samples/patxanga_no_timestamp.json").readText()
        val mariokart = TestData::class.java.getResource("/samples/mariokart.json").readText()
        
        fun patxanga(): Game {
            return parse(patxanga)
        }

        fun mariokart(): Game {
            return parse(mariokart)
        }

        private fun parse(data: String): Game {
            return Game.fromJsonBytes(data.toByteArray(Charset.defaultCharset()))
        }
    }
}