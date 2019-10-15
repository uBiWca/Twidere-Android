package org.mariotaku.twidereAntiBot.fragment.filter

import android.content.Context
import android.os.Bundle
import org.mariotaku.twidereAntiBot.constant.IntentConstants
import org.mariotaku.twidereAntiBot.constant.IntentConstants.EXTRA_ACCOUNT_KEY
import org.mariotaku.twidereAntiBot.loader.users.AbsRequestUsersLoader
import org.mariotaku.twidereAntiBot.loader.users.MutesUsersLoader
import org.mariotaku.twidereAntiBot.model.UserKey

/**
 * Created by mariotaku on 2016/12/26.
 */
class FiltersImportMutesFragment : BaseFiltersImportFragment() {

    override fun onCreateUsersLoader(context: Context, args: Bundle, fromUser: Boolean):
            AbsRequestUsersLoader {
        val accountKey = args.getParcelable<UserKey>(EXTRA_ACCOUNT_KEY)
        return MutesUsersLoader(context, accountKey, adapter.data, fromUser).apply {
            pagination = args.getParcelable(IntentConstants.EXTRA_PAGINATION)
        }
    }

}