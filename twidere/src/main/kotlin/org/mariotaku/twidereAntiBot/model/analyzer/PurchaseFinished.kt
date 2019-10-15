package org.mariotaku.twidereAntiBot.model.analyzer

import android.content.Intent
import org.mariotaku.twidereAntiBot.activity.premium.AbsExtraFeaturePurchaseActivity.Companion.EXTRA_PURCHASE_RESULT
import org.mariotaku.twidereAntiBot.model.premium.PurchaseResult
import org.mariotaku.twidereAntiBot.util.Analyzer

/**
 * Created by mariotaku on 2017/1/7.
 */

data class PurchaseFinished(val productName: String) : Analyzer.Event {
    override val name: String = "Purchase Finished"
    override var accountType: String? = null
    var price: Double = Double.NaN
    var currency: String? = null

    companion object {
        const val NAME_EXTRA_FEATURES = "Enhanced Features"

        fun create(data: Intent): PurchaseFinished {
            val purchaseResult: PurchaseResult = data.getParcelableExtra(EXTRA_PURCHASE_RESULT)
            val result = PurchaseFinished(purchaseResult.feature)
            result.price = purchaseResult.price
            result.currency = purchaseResult.currency
            return result
        }
    }
}
