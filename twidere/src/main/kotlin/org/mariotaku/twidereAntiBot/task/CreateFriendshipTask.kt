package org.mariotaku.twidereAntiBot.task

import android.content.Context
import android.widget.Toast
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.microblog.library.mastodon.Mastodon
import org.mariotaku.twidereAntiBot.Constants
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
class CreateFriendshipTask(context: Context) : AbsFriendshipOperationTask(context, FriendshipTaskEvent.Action.FOLLOW), Constants {

    @Throws(MicroBlogException::class)
    override fun perform(details: AccountDetails, args: Arguments): ParcelableUser {
        when (details.type) {
            AccountType.FANFOU -> {
                val fanfou = details.newMicroBlogInstance(context, MicroBlog::class.java)
                return fanfou.createFanfouFriendship(args.userKey.id).toParcelable(details,
                        profileImageSize = profileImageSize)
            }
            AccountType.MASTODON -> {
                val mastodon = details.newMicroBlogInstance(context, Mastodon::class.java)
                if (details.key.host != args.userKey.host) {
                    if (args.screenName == null)
                        throw MicroBlogException("Screen name required to follow remote user")
                    return mastodon.followRemoteUser("${args.screenName}@${args.userKey.host}")
                            .toParcelable(details)
                }
                mastodon.followUser(args.userKey.id)
                return mastodon.getAccount(args.userKey.id).toParcelable(details)
            }
            else -> {
                val twitter = details.newMicroBlogInstance(context, MicroBlog::class.java)
                return twitter.createFriendship(args.userKey.id).toParcelable(details,
                        profileImageSize = profileImageSize)
            }
        }
    }

    override fun succeededWorker(details: AccountDetails, args: Arguments, user: ParcelableUser) {
        user.is_following = true
        Utils.setLastSeen(context, user.key, System.currentTimeMillis())
    }

    override fun showSucceededMessage(params: AbsFriendshipOperationTask.Arguments, user: ParcelableUser) {
        val nameFirst = kPreferences[nameFirstKey]
        val message: String
        if (user.is_protected) {
            message = context.getString(R.string.sent_follow_request_to_user,
                    manager.getDisplayName(user, nameFirst))
        } else {
            message = context.getString(R.string.followed_user,
                    manager.getDisplayName(user, nameFirst))
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
