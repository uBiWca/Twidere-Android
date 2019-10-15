package org.mariotaku.twidereAntiBot.model.tab.impl

import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.annotation.TabAccountFlags
import org.mariotaku.twidereAntiBot.fragment.message.MessagesEntriesFragment
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.tab.DrawableHolder
import org.mariotaku.twidereAntiBot.model.tab.StringHolder
import org.mariotaku.twidereAntiBot.model.tab.TabConfiguration

/**
 * Created by mariotaku on 2016/11/27.
 */

class MessagesTabConfiguration : TabConfiguration() {
    override val name = StringHolder.resource(R.string.title_direct_messages)

    override val icon = DrawableHolder.Builtin.MESSAGE

    override val accountFlags = TabAccountFlags.FLAG_HAS_ACCOUNT or
            TabAccountFlags.FLAG_ACCOUNT_MULTIPLE or TabAccountFlags.FLAG_ACCOUNT_MUTABLE

    override val fragmentClass = MessagesEntriesFragment::class.java

    override fun checkAccountAvailability(details: AccountDetails) = when (details.type) {
        AccountType.TWITTER, AccountType.FANFOU, AccountType.STATUSNET -> true
        else -> false
    }
}
