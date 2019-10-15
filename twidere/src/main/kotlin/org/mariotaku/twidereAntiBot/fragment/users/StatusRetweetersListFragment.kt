/*
 *             Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidereAntiBot.fragment.users

import android.content.Context
import android.os.Bundle
import org.mariotaku.twidereAntiBot.constant.IntentConstants.EXTRA_ACCOUNT_KEY
import org.mariotaku.twidereAntiBot.constant.IntentConstants.EXTRA_STATUS_ID
import org.mariotaku.twidereAntiBot.fragment.ParcelableUsersFragment
import org.mariotaku.twidereAntiBot.loader.users.AbsRequestUsersLoader
import org.mariotaku.twidereAntiBot.loader.users.StatusRetweetersLoader
import org.mariotaku.twidereAntiBot.model.UserKey

class StatusRetweetersListFragment : ParcelableUsersFragment() {

    override fun onCreateUsersLoader(context: Context, args: Bundle, fromUser: Boolean):
            AbsRequestUsersLoader {
        val accountKey = args.getParcelable<UserKey?>(EXTRA_ACCOUNT_KEY)
        val statusId = args.getString(EXTRA_STATUS_ID)
        return StatusRetweetersLoader(context, accountKey, statusId, adapter.getData(), fromUser)
    }

}
