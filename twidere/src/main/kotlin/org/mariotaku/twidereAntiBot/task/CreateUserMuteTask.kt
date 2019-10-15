package org.mariotaku.twidereAntiBot.task

import android.content.ContentValues
import android.content.Context
import android.widget.Toast
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.microblog.library.mastodon.Mastodon
import org.mariotaku.sqliteqb.library.Expression
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.constant.nameFirstKey
import org.mariotaku.twidereAntiBot.exception.APINotSupportedException
import org.mariotaku.twidereAntiBot.extension.model.api.mastodon.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.api.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.newMicroBlogInstance
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.ParcelableUser
import org.mariotaku.twidereAntiBot.model.UserKey
import org.mariotaku.twidereAntiBot.model.event.FriendshipTaskEvent
import org.mariotaku.twidereAntiBot.provider.TwidereDataStore.*
import org.mariotaku.twidereAntiBot.util.DataStoreUtils
import org.mariotaku.twidereAntiBot.util.Utils

/**
 * Created by mariotaku on 16/3/11.
 */
class CreateUserMuteTask(
        context: Context,
        val filterEverywhere: Boolean
) : AbsFriendshipOperationTask(context, FriendshipTaskEvent.Action.MUTE) {

    @Throws(MicroBlogException::class)
    override fun perform(details: AccountDetails, args: Arguments): ParcelableUser {
        when (details.type) {
            AccountType.TWITTER -> {
                val twitter = details.newMicroBlogInstance(context, MicroBlog::class.java)
                return twitter.createMute(args.userKey.id).toParcelable(details,
                        profileImageSize = profileImageSize)
            }
            AccountType.MASTODON -> {
                val mastodon = details.newMicroBlogInstance(context, Mastodon::class.java)
                mastodon.muteUser(args.userKey.id)
                return mastodon.getAccount(args.userKey.id).toParcelable(details)
            }
            else -> throw APINotSupportedException(details.type)
        }
    }

    override fun succeededWorker(details: AccountDetails, args: Arguments, user: ParcelableUser) {
        val resolver = context.contentResolver
        Utils.setLastSeen(context, args.userKey, -1)
        for (uri in DataStoreUtils.STATUSES_URIS) {
            val where = Expression.and(
                    Expression.equalsArgs(Statuses.ACCOUNT_KEY),
                    Expression.equalsArgs(Statuses.USER_KEY)
            )
            val whereArgs = arrayOf(args.accountKey.toString(), args.userKey.toString())
            resolver.delete(uri, where.sql, whereArgs)
        }
        if (!user.is_following) {
            for (uri in DataStoreUtils.ACTIVITIES_URIS) {
                val where = Expression.and(
                        Expression.equalsArgs(Activities.ACCOUNT_KEY),
                        Expression.equalsArgs(Activities.USER_KEY)
                )
                val whereArgs = arrayOf(args.accountKey.toString(), args.userKey.toString())
                resolver.delete(uri, where.sql, whereArgs)
            }
        }
        // I bet you don't want to see this user in your auto complete list.
        val values = ContentValues()
        values.put(CachedRelationships.ACCOUNT_KEY, args.accountKey.toString())
        values.put(CachedRelationships.USER_KEY, args.userKey.toString())
        values.put(CachedRelationships.MUTING, true)
        resolver.insert(CachedRelationships.CONTENT_URI, values)
        if (filterEverywhere) {
            DataStoreUtils.addToFilter(context, listOf(user), true)
        }
    }

    override fun showSucceededMessage(params: AbsFriendshipOperationTask.Arguments, user: ParcelableUser) {
        val nameFirst = kPreferences[nameFirstKey]
        val message = context.getString(R.string.muted_user, manager.getDisplayName(user,
                nameFirst))
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        fun muteUsers(context: Context, account: AccountDetails, userKeys: Array<UserKey>) {
            when (account.type) {
                AccountType.TWITTER -> {
                    val twitter = account.newMicroBlogInstance(context, MicroBlog::class.java)
                    userKeys.forEach { userKey ->
                        twitter.createMute(userKey.id).toParcelable(account)
                    }
                }
                AccountType.MASTODON -> {
                    val mastodon = account.newMicroBlogInstance(context, Mastodon::class.java)
                    userKeys.forEach { userKey ->
                        mastodon.muteUser(userKey.id)
                    }
                }
                else -> throw APINotSupportedException(account.type)
            }
        }
    }

}
