package org.mariotaku.twidereAntiBot.task

import android.content.Context
import android.widget.Toast
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.microblog.library.mastodon.Mastodon
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.constant.nameFirstKey
import org.mariotaku.twidereAntiBot.extension.model.api.mastodon.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.api.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.newMicroBlogInstance
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.ParcelableUser
import org.mariotaku.twidereAntiBot.model.event.FriendshipTaskEvent
import org.mariotaku.twidereAntiBot.util.Utils

/**
 * Created by mariotaku on 16/3/11.
 */
class DenyFriendshipTask(context: Context) : AbsFriendshipOperationTask(context, FriendshipTaskEvent.Action.DENY) {

    @Throws(MicroBlogException::class)
    override fun perform(details: AccountDetails, args: Arguments): ParcelableUser {
        when (details.type) {
            AccountType.FANFOU -> {
                val fanfou = details.newMicroBlogInstance(context, MicroBlog::class.java)
                return fanfou.denyFanfouFriendship(args.userKey.id).toParcelable(details,
                        profileImageSize = profileImageSize)
            }
            AccountType.MASTODON -> {
                val mastodon = details.newMicroBlogInstance(context, Mastodon::class.java)
                mastodon.rejectFollowRequest(args.userKey.id)
                return mastodon.getAccount(args.userKey.id).toParcelable(details)
            }
            else -> {
                val twitter = details.newMicroBlogInstance(context, MicroBlog::class.java)
                return twitter.denyFriendship(args.userKey.id).toParcelable(details,
                        profileImageSize = profileImageSize)
            }
        }
    }

    override fun succeededWorker(details: AccountDetails, args: Arguments, user: ParcelableUser) {
        Utils.setLastSeen(context, user.key, -1)
    }

    override fun showSucceededMessage(params: AbsFriendshipOperationTask.Arguments, user: ParcelableUser) {
        val nameFirst = kPreferences[nameFirstKey]
        val message = context.getString(R.string.denied_users_follow_request,
                manager.getDisplayName(user, nameFirst))
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
