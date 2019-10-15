package org.mariotaku.twidereAntiBot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import org.mariotaku.twidereAntiBot.adapter.iface.IStatusesAdapter
import org.mariotaku.twidereAntiBot.view.holder.StatusViewHolder
import org.mariotaku.twidereAntiBot.view.holder.iface.IStatusViewHolder

/**
 * Created by mariotaku on 14/11/19.
 */
class ListParcelableStatusesAdapter(
        context: Context,
        requestManager: RequestManager
) : ParcelableStatusesAdapter(context, requestManager) {

    override fun onCreateStatusViewHolder(parent: ViewGroup): IStatusViewHolder {
        return createStatusViewHolder(this, inflater, parent)
    }

    companion object {

        fun createStatusViewHolder(adapter: IStatusesAdapter<*>,
                inflater: LayoutInflater, parent: ViewGroup): StatusViewHolder {
            val view = inflater.inflate(StatusViewHolder.layoutResource, parent, false)
            val holder = StatusViewHolder(adapter, view)
            holder.setOnClickListeners()
            holder.setupViewOptions()
            return holder
        }
    }
}
