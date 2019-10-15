package org.mariotaku.twidereAntiBot.view

import android.content.Context
import android.util.AttributeSet
import org.mariotaku.chameleon.view.ChameleonTextView
import org.mariotaku.twidereAntiBot.extension.setupEmojiFactory

/**
 * Created by mariotaku on 2017/2/3.
 */

open class FixedTextView(context: Context, attrs: AttributeSet? = null) : ChameleonTextView(context, attrs) {

    init {
        setupEmojiFactory()
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        try {
            return super.onTextContextMenuItem(id)
        } catch (e: AbstractMethodError) {
            // http://crashes.to/s/69acd0ea0de
            return true
        }
    }

}
