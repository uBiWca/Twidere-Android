package org.mariotaku.twidereAntiBot.preference

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v7.preference.Preference
import android.util.AttributeSet
import org.mariotaku.chameleon.ChameleonUtils
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.TwidereConstants.REQUEST_PURCHASE_EXTRA_FEATURES
import org.mariotaku.twidereAntiBot.extension.findParent
import org.mariotaku.twidereAntiBot.fragment.ExtraFeaturesIntroductionDialogFragment
import org.mariotaku.twidereAntiBot.util.dagger.GeneralComponent
import org.mariotaku.twidereAntiBot.util.premium.ExtraFeaturesService
import javax.inject.Inject

/**
 * Created by mariotaku on 2017/1/12.
 */

class PremiumEntryPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    @Inject
    internal lateinit var extraFeaturesService: ExtraFeaturesService

    init {
        GeneralComponent.get(context).inject(this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.PremiumEntryPreference)
        val requiredFeature = a.getString(R.styleable.PremiumEntryPreference_requiredFeature)
        a.recycle()
        isEnabled = extraFeaturesService.isSupported()
        setOnPreferenceClickListener {
            if (requiredFeature != null && !extraFeaturesService.isEnabled(requiredFeature)) {
                val activity = ChameleonUtils.getActivity(context)
                if (activity is FragmentActivity) {
                    ExtraFeaturesIntroductionDialogFragment.show(fm = activity.supportFragmentManager,
                            feature = requiredFeature, source = "preference:${key}",
                            requestCode = REQUEST_PURCHASE_EXTRA_FEATURES)
                }
                return@setOnPreferenceClickListener true
            }
            return@setOnPreferenceClickListener false
        }
    }

    override fun onAttached() {
        super.onAttached()
        if (!extraFeaturesService.isSupported()) {
            preferenceManager.preferenceScreen?.let { screen ->
                findParent(screen)?.removePreference(this)
            }
        }
    }
}
