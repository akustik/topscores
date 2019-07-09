package org.gmd

import org.gmd.model.Game
import java.nio.charset.Charset

class TestData {

    companion object {
        val patxanga = TestData::class.java.getResource("/samples/patxanga.json").readText()
        val patxanga_no_timestamp = TestData::class.java.getResource("/samples/patxanga_no_timestamp.json").readText()
        val mariokart = TestData::class.java.getResource("/samples/mariokart.json").readText()
        
        fun patxanga(): Game {
            return Game.fromJsonBytes(patxanga.toByteArray(Charset.defaultCharset()))
        }
        
        fun mariokart(): Game {
            return Game.fromJsonBytes(mariokart.toByteArray(Charset.defaultCharset()))
        }
    }
}