package org.mariotaku.twidereAntiBot.model.analyzer

import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.util.Analyzer

/**
 * Created by mariotaku on 2016/12/15.
 */

data class Search(
        val query: String,
        @AccountType override val accountType: String?,
        override val accountHost: String? = null
) : Analyzer.Event
