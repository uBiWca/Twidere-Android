package org.mariotaku.twidereAntiBot.service

import android.app.IntentService
import android.content.SharedPreferences
import com.twitter.Extractor
import org.mariotaku.twidereAntiBot.util.AsyncTwitterWrapper
import org.mariotaku.twidereAntiBot.util.NotificationManagerWrapper
import org.mariotaku.twidereAntiBot.util.UserColorNameManager
import org.mariotaku.twidereAntiBot.util.dagger.GeneralComponent
import javax.inject.Inject

abstract class BaseIntentService(tag: String) : IntentService(tag) {

    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var twitterWrapper: AsyncTwitterWrapper
    @Inject
    lateinit var notificationManager: NotificationManagerWrapper
    @Inject
    lateinit var extractor: Extractor
    @Inject
    lateinit var userColorNameManager: UserColorNameManager

    override fun onCreate() {
        super.onCreate()
        GeneralComponent.get(this).inject(this)
    }
}