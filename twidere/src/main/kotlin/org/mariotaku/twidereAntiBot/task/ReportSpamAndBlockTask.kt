package org.mariotaku.twidereAntiBot.task

import android.content.Context
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.exception.APINotSupportedException
import org.mariotaku.twidereAntiBot.extension.model.api.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.newMicroBlogInstance
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.ParcelableUser

/**
 * Created by mariotaku on 16/3/11.
 */
class ReportSpamAndBlockTask(context: Context) : CreateUserBlockTask(context) {

    @Throws(MicroBlogException::class)
    override fun perform(details: AccountDetails, args: Arguments): ParcelableUser {
        when (details.type) {
            AccountType.MASTODON -> {
                throw APINotSupportedException(details.type)
            }
            else -> {
                val twitter = details.newMicroBlogInstance(context, MicroBlog::class.java)
                return twitter.reportSpam(args.userKey.id).toParcelable(details,
                        profileImageSize = profileImageSize)
            }
        }
    }
}
