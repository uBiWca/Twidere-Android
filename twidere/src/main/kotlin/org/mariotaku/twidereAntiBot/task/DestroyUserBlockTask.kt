package org.mariotaku.twidereAntiBot.task

import android.content.ContentValues
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
import org.mariotaku.twidereAntiBot.provider.TwidereDataStore.CachedRelationships

/**
 * Created by mariotaku on 16/3/11.
 */
class DestroyUserBlockTask(context: Context) : AbsFriendshipOperationTask(context, FriendshipTaskEvent.Action.UNBLOCK) {

    @Throws(MicroBlogException::class)
    override fun perform(details: AccountDetails, args: Arguments): ParcelableUser {
        when (details.type) {
            AccountType.MASTODON -> {
                val mastodon = details.newMicroBlogInstance(context, Mastodon::class.java)
                mastodon.unblockUser(args.userKey.id)
                return mastodon.getAccount(args.userKey.id).toParcelable(details)
            }
            AccountType.FANFOU -> {
                val fanfou = details.newMicroBlogInstance(context, MicroBlog::class.java)
                return fanfou.destroyFanfouBlock(args.userKey.id).toParcelable(details,
                        profileImageSize = profileImageSize)
            }
            else -> {
                val twitter = details.newMicroBlogInstance(context, MicroBlog::class.java)
                return twitter.destroyBlock(args.userKey.id).toParcelable(details,
                        profileImageSize = profileImageSize)
            }
        }
    }

    override fun succeededWorker(details: AccountDetails,
            args: Arguments,
            user: ParcelableUser) {
        val resolver = context.contentResolver
        // I bet you don't want to see this user in your auto complete list.
        val values = ContentValues()
        values.put(CachedRelationships.ACCOUNT_KEY, args.accountKey.toString())
        values.put(CachedRelationships.USER_KEY, args.userKey.toString())
        values.put(CachedRelationships.BLOCKING, false)
        values.put(CachedRelationships.FOLLOWING, false)
        values.put(CachedRelationships.FOLLOWED_BY, false)
        resolver.insert(CachedRelationships.CONTENT_URI, values)
    }

    override fun showSucceededMessage(params: AbsFriendshipOperationTask.Arguments, user: ParcelableUser) {
        val nameFirst = kPreferences[nameFirstKey]
        val message = context.getString(R.string.unblocked_user, manager.getDisplayName(user, nameFirst))
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    }
}
