/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidereAntiBot.adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.text.BidiFormatter
import android.support.v7.widget.RecyclerView
import com.bumptech.glide.RequestManager
import org.mariotaku.kpreferences.get
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.adapter.iface.IContentAdapter
import org.mariotaku.twidereAntiBot.constant.displayProfileImageKey
import org.mariotaku.twidereAntiBot.constant.profileImageStyleKey
import org.mariotaku.twidereAntiBot.constant.showAbsoluteTimeKey
import org.mariotaku.twidereAntiBot.constant.textSizeKey
import org.mariotaku.twidereAntiBot.model.DefaultFeatures
import org.mariotaku.twidereAntiBot.util.AsyncTwitterWrapper
import org.mariotaku.twidereAntiBot.util.MultiSelectManager
import org.mariotaku.twidereAntiBot.util.ReadStateManager
import org.mariotaku.twidereAntiBot.util.UserColorNameManager
import org.mariotaku.twidereAntiBot.util.dagger.GeneralComponent
import javax.inject.Inject

/**
 * Created by mariotaku on 15/10/5.
 */
abstract class BaseRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(
        val context: Context,
        override val requestManager: RequestManager
) : RecyclerView.Adapter<VH>(), IContentAdapter {

    @Inject
    override final lateinit var twitterWrapper: AsyncTwitterWrapper

    @Inject
    override final lateinit var userColorNameManager: UserColorNameManager
    @Inject
    override final lateinit var bidiFormatter: BidiFormatter
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var readStateManager: ReadStateManager
    @Inject
    lateinit var multiSelectManager: MultiSelectManager
    @Inject
    lateinit var defaultFeatures: DefaultFeatures

    override final val profileImageSize: String = context.getString(R.string.profile_image_size)
    override final val profileImageStyle: Int
    override final val textSize: Float
    override final val profileImageEnabled: Boolean
    override final val showAbsoluteTime: Boolean

    init {
        @Suppress("UNCHECKED_CAST")
        GeneralComponent.get(context).inject(this as BaseRecyclerViewAdapter<RecyclerView.ViewHolder>)
        profileImageStyle = preferences[profileImageStyleKey]
        textSize = preferences[textSizeKey].toFloat()
        profileImageEnabled = preferences[displayProfileImageKey]
        showAbsoluteTime = preferences[showAbsoluteTimeKey]
    }

}
