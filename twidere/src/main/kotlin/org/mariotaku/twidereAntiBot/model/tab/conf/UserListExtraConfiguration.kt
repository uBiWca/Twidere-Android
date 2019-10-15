package org.mariotaku.twidereAntiBot.model.tab.conf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_extra_config_user_list.view.*
import kotlinx.android.synthetic.main.list_item_simple_user_list.view.*
import org.mariotaku.twidereAntiBot.R
import org.mariotaku.twidereAntiBot.activity.UserListSelectorActivity
import org.mariotaku.twidereAntiBot.adapter.DummyItemAdapter
import org.mariotaku.twidereAntiBot.constant.IntentConstants.*
import org.mariotaku.twidereAntiBot.fragment.CustomTabsFragment.TabEditorDialogFragment
import org.mariotaku.twidereAntiBot.model.ParcelableUserList
import org.mariotaku.twidereAntiBot.model.tab.TabConfiguration
import org.mariotaku.twidereAntiBot.util.dagger.DependencyHolder
import org.mariotaku.twidereAntiBot.view.holder.SimpleUserListViewHolder

/**
 * Created by mariotaku on 2016/11/28.
 */

class UserListExtraConfiguration(key: String) : TabConfiguration.ExtraConfiguration(key,
        R.string.title_user_list) {
    var value: ParcelableUserList? = null
        private set

    private lateinit var viewHolder: SimpleUserListViewHolder
    private lateinit var dependencyHolder: DependencyHolder
    private lateinit var hintView: View

    override fun onCreate(context: Context) {
        super.onCreate(context)
        this.dependencyHolder = DependencyHolder.get(context)
    }

    override fun onCreateView(context: Context, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_extra_config_user_list, parent, false)
    }

    override fun onViewCreated(context: Context, view: View, fragment: TabEditorDialogFragment) {
        super.onViewCreated(context, view, fragment)
        view.setOnClickListener {
            val account = fragment.account ?: return@setOnClickListener
            val intent = Intent(INTENT_ACTION_SELECT_USER_LIST)
            intent.putExtra(EXTRA_ACCOUNT_KEY, account.key)
            intent.putExtra(EXTRA_SHOW_MY_LISTS, true)
            intent.setClass(context, UserListSelectorActivity::class.java)
            fragment.startExtraConfigurationActivityForResult(this@UserListExtraConfiguration, intent, 1)
        }
        hintView = view.selectUserListHint
        val adapter = DummyItemAdapter(context, requestManager = Glide.with(context))
        viewHolder = SimpleUserListViewHolder(adapter, view.listItem)

        viewHolder.itemView.visibility = View.GONE
        hintView.visibility = View.VISIBLE
    }

    override fun onActivityResult(fragment: TabEditorDialogFragment, requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val userList: ParcelableUserList = data!!.getParcelableExtra(EXTRA_USER_LIST)
                    viewHolder.display(userList)
                    viewHolder.itemView.visibility = View.VISIBLE
                    hintView.visibility = View.GONE

                    this.value = userList
                }
            }
        }
    }
}

