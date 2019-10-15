package org.mariotaku.twidereAntiBot.extension.model

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import org.mariotaku.twidereAntiBot.model.ParcelableMedia
import org.mariotaku.twidereAntiBot.model.ParcelableMediaUpdate
import org.mariotaku.twidereAntiBot.util.BitmapFactoryUtils

/**
 * Created by mariotaku on 2016/12/7.
 */
fun ParcelableMediaUpdate.getMimeType(resolver: ContentResolver): String? {
    val uri = Uri.parse(this.uri)
    return resolver.getType(uri) ?: when (type) {
        ParcelableMedia.Type.ANIMATED_GIF -> {
            return "image/gif"
        }
        ParcelableMedia.Type.IMAGE -> {
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactoryUtils.decodeUri(resolver, uri, opts = o)
            return o.outMimeType
        }
        else -> return null
    }
}