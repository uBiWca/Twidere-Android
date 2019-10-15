package org.mariotaku.twidereAntiBot.task

import android.content.Context
import android.widget.Toast
import org.mariotaku.kpreferences.get
import org.mariotaku.ktextension.mapToArray
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.constant.nameFirstKey
import org.mariotaku.twidereAntiBot.extension.model.api.microblog.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.newMicroBlogInstance
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.ParcelableUser
import org.mariotaku.twidereAntiBot.model.ParcelableUserList
import org.mariotaku.twidereAntiBot.model.UserKey
import org.mariotaku.twidereAntiBot.model.event.UserListMembersChangedEvent

/**
 * Created by mariotaku on 2016/12/9.
 */
class AddUserListMembersTask(
        context: Context,
        accountKey: UserKey,
        private val listId: String,
        private val users: Array<out ParcelableUser>
) : AbsAccountRequestTask<Any?, ParcelableUserList, Any?>(context, accountKey) {

    override fun onExecute(account: AccountDetails, params: Any?): ParcelableUserList {
        val microBlog = account.newMicroBlogInstance(context, MicroBlog::class.java)
        val userIds = users.mapToArray(ParcelableUser::key)
        val result = microBlog.addUserListMembers(listId, UserKey.getIds(userIds))
        return result.toParcelable(account.key)
    }

    override fun onSucceed(callback: Any?, result: ParcelableUserList) {
        val message: String
        if (users.size == 1) {
            val user = users.first()
            val nameFirst = preferences[nameFirstKey]
            val displayName = userColorNameManager.getDisplayName(user.key, user.name,
                    user.screen_name, nameFirst)
            message = context.getString(R.string.message_toast_added_user_to_list, displayName, result.name)
        } else {
            val res = context.resources
            message = res.getQuantityString(R.plurals.added_N_users_to_list, users.size, users.size,
                    result.name)
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        bus.post(UserListMembersChangedEvent(UserListMembersChangedEvent.Action.ADDED, result, users))
    }

}
