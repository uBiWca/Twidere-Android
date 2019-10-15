package org.mariotaku.twidereAntiBot.task

import android.content.Context
import android.net.Uri
import android.widget.Toast
import org.mariotaku.microblog.library.MicroBlog
import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.microblog.library.mastodon.Mastodon
import org.mariotaku.microblog.library.mastodon.model.AccountUpdate
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.extension.model.api.mastodon.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.api.toParcelable
import org.mariotaku.twidereAntiBot.extension.model.newMicroBlogInstance
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.ParcelableMedia
import org.mariotaku.twidereAntiBot.model.ParcelableUser
import org.mariotaku.twidereAntiBot.model.UserKey
import org.mariotaku.twidereAntiBot.model.event.ProfileUpdatedEvent
import org.mariotaku.twidereAntiBot.task.twitter.UpdateStatusTask
import org.mariotaku.twidereAntiBot.util.DebugLog
import java.io.IOException

/**
 * Created by mariotaku on 2016/12/9.
 */
open class UpdateProfileImageTask<ResultHandler>(
        context: Context,
        accountKey: UserKey,
        private val imageUri: Uri,
        private val deleteImage: Boolean
) : AbsAccountRequestTask<Any?, ParcelableUser, ResultHandler>(context, accountKey) {

    private val profileImageSize = context.getString(R.string.profile_image_size)

    override fun onExecute(account: AccountDetails, params: Any?): ParcelableUser {
        try {
            return UpdateStatusTask.getBodyFromMedia(context, imageUri, ParcelableMedia.Type.IMAGE,
                    deleteImage, false, null, false, null).use {
                when (account.type) {
                    AccountType.MASTODON -> {
                        val mastodon = account.newMicroBlogInstance(context, Mastodon::class.java)
                        return@use mastodon.updateCredentials(AccountUpdate().avatar(it.body))
                                .toParcelable(account)
                    }
                    else -> {
                        val microBlog = account.newMicroBlogInstance(context, MicroBlog::class.java)
                        microBlog.updateProfileImage(it.body)
                        // Wait for 5 seconds, see
                        // https://dev.twitter.com/docs/api/1.1/post/account/update_profile_image
                        Thread.sleep(5000L)
                        return@use microBlog.verifyCredentials().toParcelable(account,
                                profileImageSize = profileImageSize)
                    }
                }
            }
        } catch (e: IOException) {
            throw MicroBlogException(e)
        } catch (e: InterruptedException) {
            DebugLog.w(tr = e)
            throw MicroBlogException(e)
        }
    }

    override fun onSucceed(callback: ResultHandler?, result: ParcelableUser) {
        Toast.makeText(context, R.string.message_toast_profile_image_updated, Toast.LENGTH_SHORT)
                .show()
        bus.post(ProfileUpdatedEvent(result))
    }

}
