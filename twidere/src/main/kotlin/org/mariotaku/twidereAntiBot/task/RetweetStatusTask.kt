package org.mariotaku.twidereAntiBot.task

import android.content.Context
import android.widget.Toast
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.microblog.library.mastodon.Mastodon
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.constant.TWITTER_ERROR_ALREADY_FAVORITED
import org.mariotaku.twidereAntiBot.constant.TWITTER_ERROR_ALREADY_RETWEETED
import org.mariotaku.twidereAntiBot.extension.getErrorMessage
import org.mariotaku.twidereAntiBot.extension.model.api.mastodon.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.api.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.newMicroBlogInstance
import org.mariotaku.twidereAntiBot.extension.model.updateExtraInformation
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.Draft
import org.mariotaku.twidereAntiBot.model.ParcelableStatus
import org.mariotaku.twidereAntiBot.model.UserKey
import org.mariotaku.twidereAntiBot.model.draft.StatusObjectActionExtras
import org.mariotaku.twidereAntiBot.model.event.StatusListChangedEvent
import org.mariotaku.twidereAntiBot.model.event.StatusRetweetedEvent
import org.mariotaku.twidereAntiBot.provider.TwidereDataStore.Statuses
import org.mariotaku.twidereAntiBot.task.twitter.UpdateStatusTask
import org.mariotaku.twidereAntiBot.util.AsyncTwitterWrapper
import org.mariotaku.twidereAntiBot.util.DataStoreUtils
import org.mariotaku.twidereAntiBot.util.Utils
import org.mariotaku.twidereAntiBot.util.updateStatusInfo

/**
 * Retweet status
 *
 * Created by mariotaku on 2017/2/7.
 */
class RetweetStatusTask(
        context: Context,
        accountKey: UserKey,
        private val status: ParcelableStatus
) : AbsAccountRequestTask<Any?, ParcelableStatus, Any?>(context, accountKey) {

    private val statusId = status.id

    override fun onExecute(account: AccountDetails, params: Any?): ParcelableStatus {
        val resolver = context.contentResolver
        val result = when (account.type) {
            AccountType.MASTODON -> {
                val mastodon = account.newMicroBlogInstance(context, cls = Mastodon::class.java)
                mastodon.reblogStatus(statusId).toParcelable(account)
            }
            else -> {
                val microBlog = account.newMicroBlogInstance(context, cls = MicroBlog::class.java)
                microBlog.retweetStatus(statusId).toParcelable(account)
            }
        }
        result.updateExtraInformation(account)
        Utils.setLastSeen(context, result.mentions, System.currentTimeMillis())

        resolver.updateStatusInfo(DataStoreUtils.STATUSES_ACTIVITIES_URIS, Statuses.COLUMNS,
                account.key, statusId, ParcelableStatus::class.java) { status ->
            if (statusId != status.id && statusId != status.retweet_id &&
                    statusId != status.my_retweet_id) {
                return@updateStatusInfo status
            }
            status.my_retweet_id = result.id
            status.retweeted = true
            status.reply_count = result.reply_count
            status.retweet_count = result.retweet_count
            status.favorite_count = result.favorite_count
            return@updateStatusInfo status
        }
        return result
    }

    override fun beforeExecute() {
        val hashCode = AsyncTwitterWrapper.calculateHashCode(accountKey, statusId)
        if (!creatingRetweetIds.contains(hashCode)) {
            creatingRetweetIds.add(hashCode)
        }
        bus.post(StatusListChangedEvent())
    }

    override fun afterExecute(callback: Any?, result: ParcelableStatus?, exception: MicroBlogException?) {
        creatingRetweetIds.remove(AsyncTwitterWrapper.calculateHashCode(accountKey, statusId))
        if (result != null) {
            bus.post(StatusRetweetedEvent(result))
            Toast.makeText(context, R.string.message_toast_status_retweeted, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, exception?.getErrorMessage(context), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleanup(account: AccountDetails, params: Any?, exception: MicroBlogException) {
        if (exception.errorCode == TWITTER_ERROR_ALREADY_FAVORITED) {
            val resolver = context.contentResolver

            resolver.updateStatusInfo(DataStoreUtils.STATUSES_URIS, Statuses.COLUMNS, account.key,
                    statusId, ParcelableStatus::class.java) { status ->
                status.retweeted = true
                return@updateStatusInfo status
            }
        }
    }

    override fun createDraft() = UpdateStatusTask.createDraft(Draft.Action.RETWEET) {
        account_keys = arrayOf(accountKey)
        action_extras = StatusObjectActionExtras().also { extras ->
            extras.status = this@RetweetStatusTask.status
        }
    }

    override fun deleteDraftOnException(account: AccountDetails, params: Any?, exception: MicroBlogException): Boolean {
        return exception.errorCode == TWITTER_ERROR_ALREADY_RETWEETED
    }

    companion object {

        private val creatingRetweetIds = ArrayList<Int>()

        fun isCreatingRetweet(accountKey: UserKey?, statusId: String?): Boolean {
            return creatingRetweetIds.contains(AsyncTwitterWrapper.calculateHashCode(accountKey, statusId))
        }

    }

}
