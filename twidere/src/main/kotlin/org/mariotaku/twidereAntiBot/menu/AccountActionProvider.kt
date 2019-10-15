package org.mariotaku.twidereAntiBot.menu

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.support.v4.view.ActionProvider
import android.view.Menu
import android.view.SubMenu
import org.mariotaku.twidereAntiBot.TwidereConstants
import org.mariotaku.twidereAntiBot.constant.IntentConstants.EXTRA_ACCOUNT
import org.mariotaku.twidereAntiBot.model.AccountDetails
import org.mariotaku.twidereAntiBot.model.UserKey
import org.mariotaku.twidereAntiBot.model.util.AccountUtils

class AccountActionProvider(
        context: Context,
        var accounts: Array<AccountDetails>? = AccountUtils.getAllAccountDetails(AccountManager.get(context), false)
) : ActionProvider(context), TwidereConstants {

    var selectedAccountKeys: Array<UserKey>? = null
    var isExclusive: Boolean = false

    override fun hasSubMenu(): Boolean {
        return true
    }

    override fun onPrepareSubMenu(subMenu: SubMenu) {
        subMenu.removeGroup(MENU_GROUP)
        if (accounts == null) return
        accounts?.forEachIndexed { idx, account ->
            val item = subMenu.add(MENU_GROUP, Menu.NONE, idx, account.user.name)
            val intent = Intent()
            intent.putExtra(EXTRA_ACCOUNT, account)
            item.intent = intent
        }
        subMenu.setGroupCheckable(MENU_GROUP, true, isExclusive)
        selectedAccountKeys?.let {
            for (i in 0 until subMenu.size()) {
                val item = subMenu.getItem(i)
                val intent = item.intent
                val account: AccountDetails = intent.getParcelableExtra(EXTRA_ACCOUNT)
                if (it.contains(account.key)) {
                    item.isChecked = true
                }
            }
        }
    }

    @Suppress("OverridingDeprecatedMember")
    override fun onCreateActionView() = null

    companion object {

        val MENU_GROUP = 201
    }

}
