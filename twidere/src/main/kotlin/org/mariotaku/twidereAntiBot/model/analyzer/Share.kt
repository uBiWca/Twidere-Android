package org.mariotaku.twidereAntiBot.model.analyzer

import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.annotation.ContentType
import org.mariotaku.twidereAntiBot.model.ParcelableStatus
import org.mariotaku.twidereAntiBot.util.Analyzer
import org.mariotaku.twidereAntiBot.util.LinkCreator

/**
 * Created by mariotaku on 2016/12/15.
 */

data class Share(
        val id: String,
        @ContentType val type: String,
        @AccountType override val accountType: String?,
        override val accountHost: String? = null
) : Analyzer.Event {
    companion object {
        fun status(accountType: String?, status: ParcelableStatus): Share {
            val uri = LinkCreator.getStatusWebLink(status).toString()
            return Share(uri, ContentType.STATUS, accountType, status.account_key.host)
        }
    }
}
