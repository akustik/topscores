package org.gmd

import org.gmd.model.Game
import java.nio.charset.Charset

class TestData {

    companion object {
        val patxanga = TestData::class.java.getResource("/samples/patxanga.json").readText()
        val patxanga_no_timestamp = TestData::class.java.getResource("/samples/patxanga_no_timestamp.json").readText()
        
        fun patxanga(): Game {
            return Game.fromJsonBytes(patxanga.toByteArray(Charset.defaultCharset()))
        }

        fun patxangaNoTimestamp(): Game {
            return Game.fromJsonBytes(patxanga_no_timestamp.toByteArray(Charset.defaultCharset()))
        }
    }
}