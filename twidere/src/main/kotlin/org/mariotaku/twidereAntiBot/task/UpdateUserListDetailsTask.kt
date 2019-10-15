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

package org.mariotaku.twidereAntiBot.task

import android.content.Context
import android.widget.Toast
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.twitter.model.UserListUpdate
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.extension.model.api.microblog.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.newMicroBlogInstance
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.ParcelableUserList
import org.mariotaku.twidereAntiBot.model.UserKey
import org.mariotaku.twidereAntiBot.model.event.UserListUpdatedEvent

/**
 * Created by mariotaku on 2017/2/10.
 */
class UpdateUserListDetailsTask(
        context: Context,
        accountKey: UserKey,
        private val listId: String,
        private val update: UserListUpdate
) : AbsAccountRequestTask<Any?, ParcelableUserList, Any?>(context, accountKey) {

    override fun onExecute(account: AccountDetails, params: Any?): ParcelableUserList {
        val microBlog = account.newMicroBlogInstance(context, MicroBlog::class.java)
        val list = microBlog.updateUserList(listId, update)
        return list.toParcelable(account.key)
    }

    override fun onSucceed(callback: Any?, result: ParcelableUserList) {
        val message = context.getString(R.string.updated_list_details, result.name)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        bus.post(UserListUpdatedEvent(result))
    }

}
