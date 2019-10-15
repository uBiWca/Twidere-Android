package org.mariotaku.twidereAntiBot.fragment.filter

import android.os.Bundle
import org.mariotaku.twidereAntiBot.Constants.SHARED_PREFERENCES_NAME
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.fragment.BasePreferenceFragment

class FilterSettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = SHARED_PREFERENCES_NAME
        addPreferencesFromResource(R.xml.preferences_filters)
    }

}