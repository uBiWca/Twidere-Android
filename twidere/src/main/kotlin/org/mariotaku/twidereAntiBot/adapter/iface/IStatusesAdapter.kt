package org.mariotaku.twidereAntiBot.adapter.iface

import org.mariotaku.twidereAntiBot.annotation.PreviewStyle
import org.mariotaku.twidereAntiBot.model.ParcelableStatus
import org.mariotaku.twidereAntiBot.model.UserKey
import org.mariotaku.twidereAntiBot.util.TwidereLinkify
import org.mariotaku.twidereAntiBot.view.holder.iface.IStatusViewHolder

/**
 * Created by mariotaku on 14/11/18.
 */
interface IStatusesAdapter<in Data> : IContentAdapter, IGapSupportedAdapter {

    @TwidereLinkify.HighlightStyle
    val linkHighlightingStyle: Int

    val lightFont: Boolean

    @PreviewStyle
    val mediaPreviewStyle: Int

    val twidereLinkify: TwidereLinkify

    val mediaPreviewEnabled: Boolean

    val nameFirst: Boolean

    val sensitiveContentEnabled: Boolean

    val showAccountsColor: Boolean

    val useStarsForLikes: Boolean

    val statusClickListener: IStatusViewHolder.StatusClickListener?

    fun isCardActionsShown(position: Int): Boolean

    fun showCardActions(position: Int)

    fun isFullTextVisible(position: Int): Boolean

    fun setFullTextVisible(position: Int, visible: Boolean)

    fun setData(data: Data?): Boolean

    /**
     * @param raw Count hidden (filtered) item if `true `
     */
    fun getStatusCount(raw: Boolean = false): Int

    fun getStatus(position: Int, raw: Boolean = false): ParcelableStatus

    fun getStatusId(position: Int, raw: Boolean = false): String

    fun getStatusTimestamp(position: Int, raw: Boolean = false): Long

    fun getStatusPositionKey(position: Int, raw: Boolean = false): Long

    fun getAccountKey(position: Int, raw: Boolean = false): UserKey

    fun findStatusById(accountKey: UserKey, statusId: String): ParcelableStatus?

}
