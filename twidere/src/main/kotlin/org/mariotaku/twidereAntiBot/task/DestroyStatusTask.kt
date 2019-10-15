package org.mariotaku.twidereAntiBot.task

import android.content.Context
import android.widget.Toast
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.microblog.library.mastodon.Mastodon
import org.mariotaku.microblog.library.twitter.model.ErrorInfo
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.extension.getErrorMessage
import org.mariotaku.twidereAntiBot.extension.model.api.mastodon.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.api.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.newMicroBlogInstance
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.ParcelableStatus
import org.mariotaku.twidereAntiBot.model.UserKey
import org.mariotaku.twidereAntiBot.model.event.StatusDestroyedEvent
import org.mariotaku.twidereAntiBot.model.event.StatusListChangedEvent
import org.mariotaku.twidereAntiBot.util.AsyncTwitterWrapper
import org.mariotaku.twidereAntiBot.util.DataStoreUtils
import org.mariotaku.twidereAntiBot.util.deleteActivityStatus

/**
 * Created by mariotaku on 2016/12/9.
 */
class DestroyStatusTask(
        context: Context,
        accountKey: UserKey,
        private val statusId: String
) : AbsAccountRequestTask<Any?, ParcelableStatus, Any?>(context, accountKey) {

    override fun onExecute(account: AccountDetails, params: Any?): ParcelableStatus {
        when (account.type) {
            AccountType.MASTODON -> {
                val mastodon = account.newMicroBlogInstance(context, cls = Mastodon::class.java)
                val result = mastodon.favouriteStatus(statusId)
                mastodon.deleteStatus(statusId)
                return result.toParcelable(account)
            }
            else -> {
                val microBlog = account.newMicroBlogInstance(context, cls = MicroBlog::class.java)
                return microBlog.destroyStatus(statusId).toParcelable(account)
            }
        }
    }

    override fun onCleanup(account: AccountDetails, params: Any?, result: ParcelableStatus?, exception: MicroBlogException?) {
        if (result == null && exception?.errorCode != ErrorInfo.STATUS_NOT_FOUND) return
        DataStoreUtils.deleteStatus(context.contentResolver, account.key, statusId, result)
        context.contentResolver.deleteActivityStatus(account.key, statusId, result)
    }

    override fun beforeExecute() {
        val hashCode = AsyncTwitterWrapper.calculateHashCode(accountKey, statusId)
        if (!microBlogWrapper.destroyingStatusIds.contains(hashCode)) {
            microBlogWrapper.destroyingStatusIds.add(hashCode)
        }
        bus.post(StatusListChangedEvent())
    }

    override fun afterExecute(callback: Any?, result: ParcelableStatus?, exception: MicroBlogException?) {
        microBlogWrapper.destroyingStatusIds.remove(AsyncTwitterWrapper.calculateHashCode(accountKey, statusId))
        if (result != null) {
            if (result.retweet_id != null) {
                Toast.makeText(context, R.string.message_toast_retweet_cancelled, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.message_toast_status_deleted, Toast.LENGTH_SHORT).show()
            }
            bus.post(StatusDestroyedEvent(result))
        } else {
            Toast.makeText(context, exception?.getErrorMessage(context), Toast.LENGTH_SHORT).show()
        }
    }

}
