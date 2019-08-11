package org.gmd.matchers

import org.gmd.model.Game
import org.gmd.util.JsonUtils
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class TimestampExists : BaseMatcher<String>() {
    override fun describeTo(description: Description?) {
        description!!.appendText("has a timestamp")
    }

    override fun matches(item: Any?): Boolean {
        if (item is String) {
            return JsonUtils.readValue(item, Game::class.java).timestamp!! > 0
        }

        return false
    }
}