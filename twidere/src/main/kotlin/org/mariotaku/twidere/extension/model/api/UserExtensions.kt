/*
 *             Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package org.mariotaku.twidere.extension.model.api

import android.text.TextUtils
import org.mariotaku.microblog.library.model.microblog.User
import org.mariotaku.twidere.TwidereConstants.USER_TYPE_FANFOU_COM
import org.mariotaku.twidere.TwidereConstants.USER_TYPE_TWITTER_COM
import org.mariotaku.twidere.annotation.AccountType
import org.mariotaku.twidere.model.*
import org.mariotaku.twidere.model.util.ParcelableUserUtils
import org.mariotaku.twidere.util.HtmlBuilder
import org.mariotaku.twidere.util.UriUtils
import org.mariotaku.twidere.util.Utils

fun User.getProfileImageOfSize(size: String): String {
    if ("normal" != size) {
        val larger = profileImageUrlLarge
        if (larger != null) return larger
    }
    val profileImage = profileImageUrlHttps ?: profileImageUrl
    return Utils.getTwitterProfileImageOfSize(profileImage, size)
}


fun User.toParcelable(details: AccountDetails, position: Long = 0,
        creationConfig: ModelCreationConfig = ModelCreationConfig.DEFAULT): ParcelableUser {
    return this.toParcelableInternal(details.key, details.type, position, creationConfig).apply {
        account_color = details.color
    }
}

fun User.toParcelable(accountKey: UserKey, accountType: String, position: Long = 0,
        creationConfig: ModelCreationConfig = ModelCreationConfig.DEFAULT): ParcelableUser {
    return this.toParcelableInternal(accountKey, accountType, position, creationConfig)
}

fun User.toParcelable(accountType: String, position: Long = 0,
        creationConfig: ModelCreationConfig = ModelCreationConfig.DEFAULT): ParcelableUser {
    return this.toParcelableInternal(null, accountType, position, creationConfig)
}

fun User.toParcelableInternal(accountKey: UserKey?, @AccountType accountType: String?,
        position: Long = 0, creationConfig: ModelCreationConfig = ModelCreationConfig.DEFAULT): ParcelableUser {
    val urlEntities = urlEntities
    val obj = ParcelableUser()
    obj.position = position
    obj.account_key = accountKey
    obj.key = key
    obj.created_at = createdAt?.time ?: -1
    obj.is_protected = isProtected
    obj.is_verified = isVerified
    obj.name = name
    obj.screen_name = screenName
    obj.description_plain = description
    val userDescription = formatUserDescription()
    if (userDescription != null) {
        obj.description_unescaped = userDescription.first
        obj.description_spans = userDescription.second
    }
    obj.location = location
    obj.profile_image_url = getProfileImageOfSize(creationConfig.profileImageSize)
    obj.profile_banner_url = profileBannerUrl
    obj.profile_background_url = profileBackgroundImageUrlHttps
    if (TextUtils.isEmpty(obj.profile_background_url)) {
        obj.profile_background_url = profileBackgroundImageUrl
    }
    obj.url = url
    val urlEntity = urlEntities?.find { it.url == url }
    obj.url_expanded = urlEntity?.expandedUrl
    obj.is_follow_request_sent = isFollowRequestSent == true
    obj.followers_count = followersCount
    obj.friends_count = friendsCount
    obj.statuses_count = statusesCount
    obj.favorites_count = favouritesCount
    obj.listed_count = listedCount
    obj.media_count = mediaCount
    obj.is_following = isFollowing == true
    obj.background_color = ParcelableUserUtils.parseColor(profileBackgroundColor)
    obj.link_color = ParcelableUserUtils.parseColor(profileLinkColor)
    obj.text_color = ParcelableUserUtils.parseColor(profileTextColor)
    obj.user_type = accountType
    obj.is_cache = false
    obj.is_basic = false

    val extras = ParcelableUser.Extras()
    extras.url_display = urlEntity?.displayUrl
    extras.ostatus_uri = ostatusUri
    extras.blocking = isBlocking == true
    extras.blocked_by = isBlockedBy == true
    extras.followed_by = isFollowedBy == true
    extras.muting = isMuting == true
    extras.statusnet_profile_url = statusnetProfileUrl
    extras.profile_image_url_original = profileImageUrlOriginal ?: profileImageUrlLarge
    extras.pinned_status_ids = pinnedTweetIds
    extras.groups_count = groupsCount
    extras.unique_id = uniqueId
    obj.extras = extras
    return obj
}


val User.key: UserKey
    get() = UserKey(id, this.host)

val User.host: String
    get() {
        if (isFanfouUser) return USER_TYPE_FANFOU_COM
        return getUserHost(statusnetProfileUrl, USER_TYPE_TWITTER_COM)
    }

val User.isFanfouUser: Boolean
    get() = uniqueId != null && profileImageUrlLarge != null

fun getUserHost(uri: String?, def: String?): String {
    val nonNullDef = def ?: USER_TYPE_TWITTER_COM
    if (uri == null) return nonNullDef
    val authority = UriUtils.getAuthority(uri) ?: return nonNullDef
    return authority.replace("[^\\w\\d.]".toRegex(), "-")
}

fun User.formatUserDescription(): Pair<String, Array<SpanItem>>? {
    val text = description ?: return null
    val builder = HtmlBuilder(text, false, true, false)
    val urls = descriptionEntities
    if (urls != null) {
        for (url in urls) {
            val expandedUrl = url.expandedUrl
            val displayUrl = url.displayUrl
            if (expandedUrl != null && displayUrl != null) {
                builder.addLink(expandedUrl, displayUrl, url.start, url.end, false)
            }
        }
    }
    return builder.buildWithIndices()
}