package org.mariotaku.twidereAntiBot.model.analyzer

import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.model.account.cred.Credentials
import org.mariotaku.twidereAntiBot.util.Analyzer

/**
 * Created by mariotaku on 2016/12/15.
 */
data class SignIn(
        val success: Boolean,
        val officialKey: Boolean = false,
        @Credentials.Type val credentialsType: String? = null,
        val errorReason: String? = null,
        @AccountType override val accountType: String? = null,
        override val accountHost: String? = null
) : Analyzer.Event