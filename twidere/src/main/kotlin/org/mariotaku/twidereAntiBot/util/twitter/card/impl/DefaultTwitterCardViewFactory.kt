package org.mariotaku.twidereAntiBot.util.twitter.card.impl

import org.mariotaku.twidereAntiBot.model.ParcelableStatus
import org.mariotaku.twidereAntiBot.util.twitter.card.TwitterCardViewFactory
import org.mariotaku.twidereAntiBot.view.ContainerView

/**
 * Created by mariotaku on 2017/1/25.
 */

class DefaultTwitterCardViewFactory : TwitterCardViewFactory() {
    override fun from(status: ParcelableStatus): ContainerView.ViewController? {
        return null
    }

}
