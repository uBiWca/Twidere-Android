/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package org.mariotaku.twidere.activity

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.StyleRes
import android.support.v4.view.ViewCompat
import android.support.v7.app.TwilightManagerAccessor
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec
import android.widget.Toast
import by.ubiwca.antibot.BotListIO
import by.ubiwca.antibot.BotRest
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_main.*
import nl.komponents.kovenant.Promise
import org.mariotaku.chameleon.Chameleon
import org.mariotaku.chameleon.ChameleonActivity
import org.mariotaku.kpreferences.get
import org.mariotaku.kpreferences.set
import org.mariotaku.ktextension.activityIcon
import org.mariotaku.ktextension.contains
import org.mariotaku.restfu.http.RestHttpClient
import org.mariotaku.twidere.BuildConfig
import org.mariotaku.twidere.R
import org.mariotaku.twidere.TwidereConstants.SHARED_PREFERENCES_NAME
import org.mariotaku.twidere.activity.iface.IBaseActivity
import org.mariotaku.twidere.constant.IntentConstants.EXTRA_INTENT
import org.mariotaku.twidere.constant.lastLaunchTimeKey
import org.mariotaku.twidere.constant.promotionsEnabledKey
import org.mariotaku.twidere.constant.themeColorKey
import org.mariotaku.twidere.constant.themeKey
import org.mariotaku.twidere.extension.model.displayingScore
import org.mariotaku.twidere.extension.model.hasInvalidAccount
import org.mariotaku.twidere.extension.model.shouldShow
import org.mariotaku.twidere.model.presentation.LaunchPresentation
import org.mariotaku.twidere.model.util.AccountUtils
import org.mariotaku.twidere.task.filter.RefreshLaunchPresentationsTask
import org.mariotaku.twidere.util.DeviceUtils
import org.mariotaku.twidere.util.OnLinkClickHandler
import org.mariotaku.twidere.util.StrictModeUtils
import org.mariotaku.twidere.util.ThemeUtils
import org.mariotaku.twidere.util.cache.JsonCache
import org.mariotaku.twidere.util.dagger.GeneralComponent
import org.mariotaku.twidere.util.support.ViewSupport
import org.mariotaku.twidere.util.support.view.ViewOutlineProviderCompat
import org.mariotaku.twidere.util.theme.getCurrentThemeResource
import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.thread

open class MainActivity : ChameleonActivity(), IBaseActivity<MainActivity> {

    @Inject
    lateinit var restHttpClient: RestHttpClient

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var jsonCache: JsonCache

    private val handler = Handler(Looper.getMainLooper())
    private val launchLaterRunnable: Runnable = Runnable { launchMain() }

    private val actionHelper = IBaseActivity.ActionHelper<MainActivity>()

    private var isNightBackup: Int = TwilightManagerAccessor.UNSPECIFIED

    private val themePreferences by lazy {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private val userTheme: Chameleon.Theme by lazy {
        return@lazy ThemeUtils.getUserTheme(this, themePreferences)
    }

    private lateinit var requestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            StrictModeUtils.detectAllVmPolicy()
            StrictModeUtils.detectAllThreadPolicy()
        }
        val themeColor = themePreferences[themeColorKey]
        val themeResource = getThemeResource(themePreferences, themePreferences[themeKey], themeColor)
        if (themeResource != 0) {
            setTheme(themeResource)
        }
        super.onCreate(savedInstanceState)
        GeneralComponent.get(this).inject(this)
        requestManager = Glide.with(this)
        setContentView(R.layout.activity_main)

        // Checking for updates on bots database. No more than once a day-------------------------
        thread {
            Log.d("MainActivity", "Going to check list updates")
            val botIO = BotListIO(applicationContext)
            val recordsCount = botIO.recordsCount()
            botIO.clear()
            if (recordsCount < 10) {

                //Unconditional DB update
                thread {
                    val botList = BotRest("https://blocktogether.org/show-blocks/SiJai3FyVmodO0XxkL2r-pezIK_oahHRwqv9I6U3.csv").getBlockList()
                    //   val  = botRest.getBlockList()
                    Log.d("MainActivity", "DB updates contains ${botList.size} records")
                    botIO.updateDB(botList)
                    botIO.clear()
                    val antiBotPreferences = getSharedPreferences(resources.getString(R.string.antiBot_preferences_name), Context.MODE_PRIVATE)
                    val pref_editor = antiBotPreferences.edit()
                    pref_editor.putLong("Timestamp",Timestamp(System.currentTimeMillis()).time )
                    pref_editor.apply()

                }

            } else {
                thread {
                    // If about 24h since last update is passed, updating DB
                    val antiBotTimestamp = Timestamp(System.currentTimeMillis()).time
                    val antiBotPreferences = getSharedPreferences(resources.getString(R.string.antiBot_preferences_name), Context.MODE_PRIVATE)
                    val timeStmp = antiBotPreferences.getLong("Timestamp", 0)
                    Log.d("MainActivity", "Current timestamp is $antiBotTimestamp and previous timestamp is $timeStmp")
                    val diff = antiBotTimestamp - timeStmp
                    Log.d("MainActivity", "Stamps difference $diff")
                    if ((antiBotTimestamp - timeStmp) > 86000000L) {
                        // do update DB
                        Log.d("MainActivity", "Updating botlist")
                        val botList = BotRest("https://blocktogether.org/show-blocks/SiJai3FyVmodO0XxkL2r-pezIK_oahHRwqv9I6U3.csv").getBlockList()
                        Log.d("MainActivity", "DB updates contains ${botList.size} records")
                        botIO.updateDB(botList)
                        botIO.clear()
                        val pref_editor = antiBotPreferences.edit()
                        pref_editor.putLong("Timestamp",Timestamp(System.currentTimeMillis()).time )
                        pref_editor.apply()


                    }
                }


            }

        }
   /*     Log.d("MainActivity", "Going to check list updates")
        val antiBotTimestamp = Timestamp(System.currentTimeMillis()).time
        val antiBotPreferences = getSharedPreferences(resources.getString(R.string.antiBot_preferences_name), Context.MODE_PRIVATE)
        val timeStmp = antiBotPreferences.getLong("Timestamp", 0)
        thread {
            val botIO = BotListIO(applicationContext)
            val botList = BotRest("https://blocktogether.org/show-blocks/SiJai3FyVmodO0XxkL2r-pezIK_oahHRwqv9I6U3.csv").getBlockList()
            //   val  = botRest.getBlockList()
            Log.d("MainActivity", "DB updates contains ${botList.size} records")
            botIO.updateDB(botList)
            botIO.clear()
        }

        Log.d("MainActivity", timeStmp.toString())*/
        /*   if (timeStmp == 0L) {
               val editor = antiBotPreferences.edit()
               editor.putLong("Timestamp", antiBotTimestamp )
               editor.commit()
               Log.d("MainActivity", "Timestamp is 0, updating botlist")
               // update anyway
               val botRest = BotRest("https://blocktogether.org/show-blocks/SiJai3FyVmodO0XxkL2r-pezIK_oahHRwqv9I6U3.csv")
               val botList = botRest.getBlockList()
               val blio = BotListIO()
               blio.setBotList(botList)
               blio.saveListToFile()

           } else {
               if ((antiBotTimestamp - timeStmp) > 86000000L) {
                   // do update DB
                   Log.d("MainActivity", "Updating botlist")
                   val botRest = BotRest("https://blocktogether.org/show-blocks/SiJai3FyVmodO0XxkL2r-pezIK_oahHRwqv9I6U3.csv")
                   val botList = botRest.getBlockList()
                   val blio = BotListIO()
                   blio.setBotList(botList)
                   blio.saveListToFile()


               }
           }*/
        //----------------------------------------------------------------------------------------


        if (!preferences[promotionsEnabledKey]) {
            main.visibility = View.GONE
            launchDirectly()
            return
        }

        main.visibility = View.VISIBLE
        appIcon.setImageDrawable(activityIcon)
        skipPresentation.setOnClickListener {
            launchDirectly()
        }
        if (BuildConfig.DEBUG) {
            skipPresentation.setOnLongClickListener {
                handler.removeCallbacks(launchLaterRunnable)
                return@setOnLongClickListener true
            }
        }
        controlOverlay.setOnClickListener {
            val presentation = controlOverlay.tag as? LaunchPresentation ?: return@setOnClickListener
            val uri = presentation.url?.let(Uri::parse) ?: return@setOnClickListener
            OnLinkClickHandler.openLink(this, preferences, uri)
        }

        ViewCompat.setOnApplyWindowInsetsListener(main) lambda@ { _, insets ->
            main.setPadding(0, 0, insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom)

            controlOverlay.setPadding(0, insets.systemWindowInsetTop, 0, 0)
            return@lambda insets.consumeSystemWindowInsets()
        }
        ViewSupport.setOutlineProvider(skipPresentation, ViewOutlineProviderCompat.BACKGROUND)

        showPresentationOrLaunch()
    }

    private fun showPresentationOrLaunch() {
        val lastLaunchTime = preferences[lastLaunchTimeKey]
        val maximumDuration = if (BuildConfig.DEBUG) {
            TimeUnit.SECONDS.toMillis(30)
        } else {
            TimeUnit.HOURS.toMillis(6)
        }
        // Show again at least 6 hours later (30 secs for debug builds)
        if (lastLaunchTime >= 0 && System.currentTimeMillis() - lastLaunchTime < maximumDuration) {
            launchDirectly()
            return
        }
        val presentation = jsonCache.getList(RefreshLaunchPresentationsTask.JSON_CACHE_KEY,
                LaunchPresentation::class.java)?.firstOrNull {
            it.shouldShow(this)
        }
        if (presentation != null && displayPresentation(presentation)) {
            launchLater()
        } else {
            launchDirectly()
        }
    }

    override fun onStart() {
        super.onStart()
        requestManager.onStart()
    }

    override fun onStop() {
        requestManager.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        requestManager.onDestroy()
        super.onDestroy()
    }

    override fun onPause() {
        actionHelper.dispatchOnPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        updateNightMode()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        actionHelper.dispatchOnResumeFragments(this)
    }

    override fun executeAfterFragmentResumed(useHandler: Boolean, action: (MainActivity) -> Unit): Promise<Unit, Exception> {
        return actionHelper.executeAfterFragmentResumed(this, useHandler, action)
    }

    override fun getOverrideTheme(): Chameleon.Theme? {
        return userTheme
    }

    @StyleRes
    protected open fun getThemeResource(preferences: SharedPreferences, theme: String, themeColor: Int): Int {
        return getCurrentThemeResource(this, theme)
    }

    private fun updateNightMode() {
        val nightState = TwilightManagerAccessor.getNightState(this)
        if (isNightBackup != TwilightManagerAccessor.UNSPECIFIED && nightState != isNightBackup) {
            recreate()
            return
        }
        isNightBackup = nightState
    }

    private fun displayPresentation(presentation: LaunchPresentation): Boolean {
        skipPresentation.visibility = View.VISIBLE
        controlOverlay.tag = presentation

        val dm = resources.displayMetrics
        main.measure(MeasureSpec.makeMeasureSpec(dm.widthPixels,
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dm.heightPixels,
                MeasureSpec.EXACTLY))

        val width = presentationView.measuredWidth
        val height = presentationView.measuredHeight
        val images = presentation.images.sortedByDescending { it.displayingScore(dm.density, width, height) }
        val image = images.firstOrNull() ?: return false

        Glide.with(this).load(image.url)
                .priority(Priority.HIGH)
                .into(presentationView)
        return true
    }

    private fun launchDirectly() {
        handler.removeCallbacks(launchLaterRunnable)
        launchMain()
    }

    private fun launchLater() {
        handler.postDelayed(launchLaterRunnable, 5000L)
    }

    private fun launchMain() {
        if (isFinishing) return
        executeAfterFragmentResumed { performLaunch() }
    }

    private fun performLaunch() {
        preferences[lastLaunchTimeKey] = System.currentTimeMillis()
        val am = AccountManager.get(this)
        if (!DeviceUtils.checkCompatibility()) {
            startActivity(Intent(this, IncompatibleAlertActivity::class.java))
        } else if (!AccountUtils.hasAccountPermission(am)) {
            Toast.makeText(this, R.string.message_toast_no_account_permission, Toast.LENGTH_SHORT).show()
        } else if (am.hasInvalidAccount()) {
            val intent = Intent(this, InvalidAccountAlertActivity::class.java)
            intent.putExtra(EXTRA_INTENT, Intent(this, HomeActivity::class.java))
            startActivity(intent)
        } else {
            if (ApplicationInfo.FLAG_EXTERNAL_STORAGE in packageManager.getApplicationInfo(packageName, 0).flags) {
                Toast.makeText(this, R.string.message_toast_internal_storage_install_required, Toast.LENGTH_LONG).show()
            }
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        finish()
    }

}

