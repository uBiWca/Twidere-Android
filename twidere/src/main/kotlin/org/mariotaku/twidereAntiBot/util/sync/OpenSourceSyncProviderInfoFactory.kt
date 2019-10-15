package org.mariotaku.twidereAntiBot.util.sync

import android.content.Context
import android.content.SharedPreferences
import org.mariotaku.twidereAntiBot.model.sync.SyncProviderEntry

/**
 * Created by mariotaku on 2017/1/2.
 */

class OpenSourceSyncProviderInfoFactory : DataSyncProvider.Factory() {
    override fun createForType(type: String, preferences: SharedPreferences): DataSyncProvider? {
        return null
    }

    override fun getSupportedProviders(context: Context): List<SyncProviderEntry> {
        return emptyList()
    }

    override fun notifyUpdate(context: Context) {

    }

}
