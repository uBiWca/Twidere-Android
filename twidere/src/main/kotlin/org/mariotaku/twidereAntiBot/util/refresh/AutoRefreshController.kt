package org.mariotaku.twidereAntiBot.util.refresh

import android.content.Context
import org.mariotaku.kpreferences.KPreferences
import org.mariotaku.twidereAntiBot.annotation.AutoRefreshType

/**
 * Created by mariotaku on 2016/12/17.
 */

abstract class AutoRefreshController(
        val context: Context,
        val kPreferences: KPreferences
) {

    abstract fun appStarted()

    abstract fun schedule(@AutoRefreshType type: String)

    abstract fun unschedule(@AutoRefreshType type: String)

    open fun reschedule(@AutoRefreshType type: String) {
        unschedule(type)
        schedule(type)
    }

    open fun rescheduleAll() {
        AutoRefreshType.ALL.forEach { reschedule(it) }
    }

}
