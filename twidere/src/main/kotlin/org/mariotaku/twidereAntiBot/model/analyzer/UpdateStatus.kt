package org.mariotaku.twidereAntiBot.model.analyzer

import org.mariotaku.microblog.library.MicroBlogException
import org.mariotaku.twidereAntiBot.annotation.AccountType
import org.mariotaku.twidereAntiBot.extension.model.draftActionTypeString
import org.mariotaku.twidereAntiBot.extension.model.parcelableMediaTypeString
import org.mariotaku.twidereAntiBot.model.Draft
import org.mariotaku.twidereAntiBot.model.ParcelableMedia
import org.mariotaku.twidereAntiBot.task.twitter.UpdateStatusTask
import org.mariotaku.twidereAntiBot.util.Analyzer
import java.io.IOException

/**
 * Created by mariotaku on 2016/12/28.
 */

data class UpdateStatus(
        @AccountType override val accountType: String? = null,
        @Draft.Action val actionType: String?,
        @ParcelableMedia.Type val mediaType: Int,
        val hasLocation: Boolean,
        val preciseLocation: Boolean,
        val success: Boolean,
        val exception: Exception?
) : Analyzer.Event {

    private val locationType: String get() = if (!hasLocation) {
        "none"
    } else if (preciseLocation) {
        "coordinate"
    } else {
        "place"
    }

    private val errorReason: String? get() {
        val ex = exception ?: return null
        when (ex) {
            is UpdateStatusTask.ShortenerNotFoundException,
            is UpdateStatusTask.UploaderNotFoundException ->
                return "extension not found"
            else -> {
                val cause = ex.cause
                when (cause) {
                    is UpdateStatusTask.ExtensionVersionMismatchException ->
                        return "extension version mismatch"
                    is IOException ->
                        return "io exception"
                    is MicroBlogException -> {
                        if (cause.isCausedByNetworkIssue) {
                            return "network error"
                        }
                        return "request error"
                    }
                }
                when (ex) {
                    is UpdateStatusTask.ShortenException,
                    is UpdateStatusTask.UploadException ->
                        return "extension error"
                }
                return "internal error"
            }
        }
    }

    override val name: String
        get() = "Tweet"

    override fun forEachValues(action: (String, String?) -> Unit) {
        action("Status Type", draftActionTypeString(actionType))
        action("Media Type", parcelableMediaTypeString(mediaType))
        action("Location Type", locationType)
        action("Success", success.toString())

    }

}
