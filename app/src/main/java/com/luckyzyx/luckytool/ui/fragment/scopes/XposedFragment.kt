package com.luckyzyx.luckytool.ui.fragment.scopes

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.utils.scopeLife
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.FragmentItem
import com.luckyzyx.luckytool.data.PrefsItem
import com.luckyzyx.luckytool.databinding.DialogScopeVersionInfoBinding
import com.luckyzyx.luckytool.databinding.DialogSearchResultLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutSearchResultItemBinding
import com.luckyzyx.luckytool.listener.OnSelectSearchResultListener
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusBattery
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusBreenoTouch
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusBrowser
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusCalendar
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusCamera
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusCloudService
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusEyeProtect
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusGallery
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusGames
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusGesture
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusMMS
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusMarket
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusOTA
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusPhoneManager
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusPictorial
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusScreenshot
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusSearchBox
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusSettings
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusSmartSidebar
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusSoundRecorder
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusTeleService
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusThemeStore
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusWeather
import com.luckyzyx.luckytool.ui.fragment.scopes.others.ADM
import com.luckyzyx.luckytool.ui.fragment.scopes.others.AlphaBackupPro
import com.luckyzyx.luckytool.ui.fragment.scopes.others.KsWeb
import com.luckyzyx.luckytool.ui.fragment.scopes.related.AndroidRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.AodRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.ApplicationRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.CorePatch
import com.luckyzyx.luckytool.ui.fragment.scopes.related.DialogRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.FingerPrintRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.LauncherRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.LockScreenRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.Miscellaneous
import com.luckyzyx.luckytool.ui.fragment.scopes.related.SoundRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.StatusBarRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarBattery
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarClock
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarControlCenter
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarIcon
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarLayout
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarNetWorkSpeed
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarNotify
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarNotifyRemoval
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarTiles
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.RestartMenuUtils
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.fixIconSize
import com.luckyzyx.luckytool.utils.formatStringAuto
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.safeOf
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import com.luckyzyx.luckytool.utils.setupMenuProvider
import com.luckyzyx.luckytool.utils.showBottomSheet
import com.luckyzyx.luckytool.utils.showToast
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.util.Arrays

@Obfuscate
class XposedFragment : BaseScopePreferenceFeagment(), MenuProvider {

    override val currentPrefsName: String = ModulePrefs

    private lateinit var navController: NavController

    private val allFragmentItem = ArrayList<FragmentItem>()

    private var loadDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        setupMenuProvider(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            //Android
            add(Preference(this@loadPreferences).apply {
                key = "android"
                setPrefsIconRes(android.R.mipmap.sym_def_app_icon) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.allow_untrusted_touch),
                    getString(R.string.set_ltpo_refresh_rate_mode)
                )
                setOnPreferenceClickListener {
                    navigatePage(R.id.androidRelated, title)
                    true
                }
            })
            //StatusBar
            add(Preference(this@loadPreferences).apply {
                key = "StatusBar"
                setPrefsIconRes("com.android.systemui") { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = getString(R.string.StatusBar)
                summary = arraySummaryDot(
                    getString(R.string.StatusBarNotice),
                    getString(R.string.StatusBarIcon),
                    getString(R.string.StatusBarClock)
                )
                setOnPreferenceClickListener {
                    navigatePage(R.id.statusBar, title)
                    true
                }
            })
            //Launcher
            add(Preference(this@loadPreferences).apply {
                key = "com.android.launcher"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = getString(R.string.Desktop)
                summary = arraySummaryDot(
                    getString(R.string.AppBadgeRelated),
                    getString(R.string.FolderLayoutRelated),
                    getString(R.string.launcher_layout_related)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.launcher, title)
                    true
                }
            })
            //Aod
            add(Preference(this@loadPreferences).apply {
                key = "com.oplus.aod"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = getString(R.string.AodRelated)
                summary = arraySummaryDot(
                    getString(R.string.remove_aod_music_whitelist),
                    getString(R.string.remove_aod_notification_icon_whitelist)
                )
                isVisible = SDK >= A13 && checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.aod, title)
                    true
                }
            })
            //LockScreen
            add(Preference(this@loadPreferences).apply {
                key = "LockScreen"
                setPrefsIconRes("com.android.systemui") { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = getString(R.string.LockScreen)
                summary = arraySummaryDot(
                    getString(R.string.lock_screen_clock_redone_mode),
                    getString(R.string.remove_lock_screen_bottom_right_camera)
                )
                setOnPreferenceClickListener {
                    navigatePage(R.id.lockScreen, title)
                    true
                }
            })
            //Application
            add(Preference(this@loadPreferences).apply {
                key = "com.android.packageinstaller"
                setPrefsIconRes(key) { resource, show ->
                    icon = fixIconSize(resource)
                    isIconSpaceReserved = show
                }
                title = getString(R.string.Application)
                summary = arraySummaryDot(
                    getString(R.string.skip_apk_scan),
                    getString(R.string.unlock_startup_limit)
                )
                setOnPreferenceClickListener {
                    navigatePage(R.id.application, title)
                    true
                }
            })
            //Miscellaneous
            add(Preference(this@loadPreferences).apply {
                key = "Miscellaneous"
                setPrefsIconRes("com.android.systemui") { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = getString(R.string.Miscellaneous)
                summary =
                    arraySummaryDot(getString(R.string.Miscellaneous_summary))
                setOnPreferenceClickListener {
                    navigatePage(R.id.miscellaneous, title)
                    true
                }
            })
            //Screenshot
            add(Preference(this@loadPreferences).apply {
                key = "com.oplus.screenshot"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_system_screenshot_delay),
                    getString(R.string.remove_screenshot_privacy_limit)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusScreenshot, title)
                    true
                }
            })
            //Battery
            add(Preference(this@loadPreferences).apply {
                key = "com.oplus.battery"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.open_battery_health),
                    getString(R.string.remove_battery_temperature_control)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusBattery, title)
                    true
                }
            })
            //Settings
            add(Preference(this@loadPreferences).apply {
                key = "com.android.settings"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_top_account_display),
                    getString(R.string.remove_dpi_restart_recovery)
                )
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusSettings, title)
                    true
                }
            })
            //TeleService
            add(Preference(this@loadPreferences).apply {
                key = "com.android.phone"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.force_display_five_g_switch),
                    getString(R.string.force_display_preferred_network_type)
                )
                isVisible = SDK >= A13 && checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusTeleService, title)
                    true
                }
            })
            //Mms
            add(Preference(this@loadPreferences).apply {
                key = "com.android.mms"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary =
                    arraySummaryDot(getString(R.string.remove_verification_code_floating_window))
                isVisible = SDK >= A13 && checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusMMS, title)
                    true
                }
            })
            //Browser
            add(Preference(this@loadPreferences).apply {
                key = "com.heytap.browser"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_ads_from_download_dialog),
                    getString(R.string.remove_ads_at_download_page_bottom),
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusBrowser, title)
                    true
                }
            })
            //Camera
            add(Preference(this@loadPreferences).apply {
                val isOneplusCamera = checkPackName("com.oneplus.camera")
                key = if (isOneplusCamera) "com.oneplus.camera" else "com.oplus.camera"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_watermark_word_limit),
                    getString(R.string.enable_10_bit_image_support)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusCamera, title)
                    true
                }
            })
            //Gallery
            add(Preference(this@loadPreferences).apply {
                key = "com.coloros.gallery3d"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.enable_watermark_editing),
                    getString(R.string.enable_lns_cut_photo)
                )
                isVisible = getOSVersionCode >= 27 && checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusGallery, title)
                    true
                }
            })
            //Games
            add(Preference(this@loadPreferences).apply {
                key = "com.oplus.games"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_root_check),
                    getString(R.string.enable_developer_page)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusGames, title)
                    true
                }
            })
            //Theme
            add(Preference(this@loadPreferences).apply {
                val isHeytap = checkPackName("com.heytap.themestore")
                key = if (isHeytap) "com.heytap.themestore" else "com.oplus.themestore"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.unlock_themestore_vip)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.themeStore, title)
                    true
                }
            })
            //Market
            add(Preference(this@loadPreferences).apply {
                key = "com.heytap.market"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.remove_market_splash_page_app_recommend),
                    getString(R.string.remove_market_update_download_page_app_recommend)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusMarket, title)
                    true
                }
            })
            //CloudService
            add(Preference(this@loadPreferences).apply {
                key = "com.heytap.cloud"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_network_limit)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusCloudService, title)
                    true
                }
            })
            //OTA
            add(Preference(this@loadPreferences).apply {
                key = "com.oplus.ota"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.unlock_local_upgrade),
                    getString(R.string.restore_ota_update_verity)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusOta, title)
                    true
                }
            })
            //Pictorial
            add(Preference(this@loadPreferences).apply {
                key = "com.heytap.pictorial"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_image_save_watermark),
                    getString(R.string.remove_video_save_watermark)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusPictorial, title)
                    true
                }
            })
            //Gesture
            add(Preference(this@loadPreferences).apply {
                key = "com.oplus.gesture"
                setPrefsIconRes(key) { resource, show ->
                    icon = fixIconSize(resource)
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.enable_volume_key_control_flashlight),
                    getString(R.string.force_enable_aon_gestures)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusGesture, title)
                    true
                }
            })
            //Directui
            add(Preference(this@loadPreferences).apply {
                key = "com.coloros.directui"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_app_recommend_card),
                )
                isVisible = checkPackName(key) && checkPackName(
                    "com.coloros.colordirectservice"
                )
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusBreenoTouch, title)
                    true
                }
            })
            //QuickSearchBox
            add(Preference(this@loadPreferences).apply {
                key = "com.heytap.quicksearchbox"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_app_recommend_card),
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusSearchBox, title)
                    true
                }
            })
            //Weather
            add(Preference(this@loadPreferences).apply {
                key = "com.coloros.weather2"
                setPrefsIconRes(key) { resource, show ->
                    icon = fixIconSize(resource)
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.disable_weather_jump_browser),
                    getString(R.string.remove_weather_some_page_bottom_ads)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusWeather, title)
                    true
                }
            })
            //Calendar
            add(Preference(this@loadPreferences).apply {
                key = "com.coloros.calendar"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.remove_holiday_page_information_flow),
                    getString(R.string.remove_almanac_page_information_flow)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusCalendar, title)
                    true
                }
            })
            //SmartSidebar
            add(Preference(this@loadPreferences).apply {
                key = "com.coloros.smartsidebar"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.unlock_transfer_dock),
                    getString(R.string.unlock_recent_files)
                )
                isVisible = SDK >= A12 && checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusSmartSidebar, title)
                    true
                }
            })
            //PhoneManager
            add(Preference(this@loadPreferences).apply {
                key = "com.coloros.phonemanager"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.remove_secure_pay_found_virus_dialog),
                    getString(R.string.remove_virus_risk_notification_in_phone_manager)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusPhoneManager, title)
                    true
                }
            })
            //SoundRecorder
            add(Preference(this@loadPreferences).apply {
                key = "com.coloros.soundrecorder"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.enable_record_calls_on_third_party_apps)
                )
                isVisible = getOSVersionCode >= 30 && checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusSoundRecorder, title)
                    true
                }
            })
            //EyeProtect
            add(Preference(this@loadPreferences).apply {
                key = "com.oplus.eyeprotect"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryLine(
                    getString(R.string.enable_eyeprotect_paper_texture_support)
                )
                isVisible = getOSVersionCode >= 33 && checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.oplusEyeProtect, title)
                    true
                }
            })
            //Other App
            add(Preference(this@loadPreferences).apply {
                key = "com.ruet_cse_1503050.ragib.appbackup.pro"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_pro_license)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.alphaBackupPro, title)
                    true
                }
            })
            add(Preference(this@loadPreferences).apply {
                key = "ru.kslabs.ksweb"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.remove_pro_license)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.ksWeb, title)
                    true
                }
            })
            add(Preference(this@loadPreferences).apply {
                key = "com.dv.adm"
                setPrefsIconRes(key) { resource, show ->
                    icon = resource
                    isIconSpaceReserved = show
                }
                title = AppUtils(this@loadPreferences).getAppLabel(key)
                summary = arraySummaryDot(
                    getString(R.string.adm_unlock_pro)
                )
                isVisible = checkPackName(key)
                setOnPreferenceClickListener {
                    navigatePage(R.id.adm, title)
                    true
                }
            })
        }
    }

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        if (loadDialog == null) loadDialog =
            MaterialAlertDialogBuilder(requireActivity(), dialogCentered).apply {
                setTitle(getString(R.string.common_words_loading))
                setView(LinearLayout(context).apply {
                    addView(LinearProgressIndicator(context).apply {
                        layoutParams =
                            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                        setPadding(20.dp)
                        isIndeterminate = true
                    })
                })
            }.create()
    }

    override fun onResume() {
        super.onResume()
        val count = safeOf(0) {
            if (preferenceScreen != null) preferenceScreen.preferenceCount else 0
        }
        if (count <= 0) init()
    }

    private fun initAllScopePreferences() {
        allFragmentItem.clear()

        addFragmentItem(requireActivity(), allFragmentItem, StatusBarRelated())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarBattery())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarClock())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarControlCenter())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarIcon())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarLayout())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarNetWorkSpeed())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarNotify())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarNotifyRemoval())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarTiles())

        addFragmentItem(requireActivity(), allFragmentItem, AndroidRelated())
        addFragmentItem(requireActivity(), allFragmentItem, CorePatch())
        addFragmentItem(requireActivity(), allFragmentItem, AodRelated())
        addFragmentItem(requireActivity(), allFragmentItem, ApplicationRelated())
        addFragmentItem(requireActivity(), allFragmentItem, DialogRelated())
        addFragmentItem(requireActivity(), allFragmentItem, FingerPrintRelated())
        addFragmentItem(requireActivity(), allFragmentItem, LauncherRelated())
        addFragmentItem(requireActivity(), allFragmentItem, LockScreenRelated())
        addFragmentItem(requireActivity(), allFragmentItem, Miscellaneous())

        addFragmentItem(requireActivity(), allFragmentItem, OplusBattery())
        addFragmentItem(requireActivity(), allFragmentItem, OplusBreenoTouch())
        addFragmentItem(requireActivity(), allFragmentItem, OplusBrowser())
        addFragmentItem(requireActivity(), allFragmentItem, OplusCalendar())
        addFragmentItem(requireActivity(), allFragmentItem, OplusCamera())
        addFragmentItem(requireActivity(), allFragmentItem, OplusCloudService())
        addFragmentItem(requireActivity(), allFragmentItem, OplusEyeProtect())
        addFragmentItem(requireActivity(), allFragmentItem, OplusGallery())
        addFragmentItem(requireActivity(), allFragmentItem, OplusGames())
        addFragmentItem(requireActivity(), allFragmentItem, OplusGesture())
        addFragmentItem(requireActivity(), allFragmentItem, OplusMarket())
        addFragmentItem(requireActivity(), allFragmentItem, OplusMMS())
        addFragmentItem(requireActivity(), allFragmentItem, OplusOTA())
        addFragmentItem(requireActivity(), allFragmentItem, OplusPhoneManager())
        addFragmentItem(requireActivity(), allFragmentItem, OplusPictorial())
        addFragmentItem(requireActivity(), allFragmentItem, OplusScreenshot())
        addFragmentItem(requireActivity(), allFragmentItem, OplusSearchBox())
        addFragmentItem(requireActivity(), allFragmentItem, OplusSettings())
        addFragmentItem(requireActivity(), allFragmentItem, OplusSmartSidebar())
        addFragmentItem(requireActivity(), allFragmentItem, OplusSoundRecorder())
        addFragmentItem(requireActivity(), allFragmentItem, OplusTeleService())
        addFragmentItem(requireActivity(), allFragmentItem, OplusWeather())
        addFragmentItem(requireActivity(), allFragmentItem, SoundRelated())
        addFragmentItem(requireActivity(), allFragmentItem, OplusThemeStore())

        addFragmentItem(requireActivity(), allFragmentItem, ADM())
        addFragmentItem(requireActivity(), allFragmentItem, AlphaBackupPro())
        addFragmentItem(requireActivity(), allFragmentItem, KsWeb())

    }

    private fun addFragmentItem(
        context: Context,
        list: ArrayList<FragmentItem>,
        fragment: BaseScopePreferenceFeagment
    ) {
        if (fragment.navigateFragmentId == -1) return
        list.add(
            FragmentItem(
                fragment, fragment.navigateFragmentId,
                fragment.readPrefsItem(context)
            )
        )
    }

    private fun init() {
        loadDialog?.show()
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            context.loadPreferences().forEachIndexed { index, preference ->
                try {
                    addPreference(preference)
                } catch (_: Throwable) {
                    context.showToast("Error: $index ${preference.key}")
                    return@forEachIndexed
                }
            }
        }
        loadDialog?.dismiss()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, "Search").apply {
            setIcon(R.drawable.ic_baseline_search_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 2, 0, getString(R.string.menu_reboot)).apply {
            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 3, 0, getString(R.string.menu_versioninfo)).apply {
            setIcon(R.drawable.ic_baseline_extension_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            1 -> requireActivity().showSearchDialog()
            2 -> RestartMenuUtils.showMainRestartMenu(requireActivity())
            3 -> requireActivity().showBottomDialog()
        }
        return true
    }

    private fun Context.showBottomDialog() {
        scopeLife {
            val dialogBinding = DialogScopeVersionInfoBinding.inflate(layoutInflater)
            val markwon = Markwon.builder(this@showBottomDialog).apply {
                usePlugin(TablePlugin.create(this@showBottomDialog))
            }.build()
            showBottomSheet(dialogBinding.root)
            val list = ArrayList<String>().apply {
                add("| name | package | version |")
                add("| ------ | ------ | ------|")
            }
            val xposedScope = resources.getStringArray(R.array.xposed_scope)
            Arrays.sort(xposedScope)
            xposedScope.forEach {
                val appVerInfo = AppUtils(this@showBottomDialog).getAppVerInfo(it)
                    ?: return@forEach
                list.add("| ${appVerInfo.appName} | $it |  ${appVerInfo.versionName}(${appVerInfo.versionCode})[${appVerInfo.versionCommit}] |")
            }
            markwon.setMarkdown(dialogBinding.tv, formatStringAuto(list, "\n"))
        }
    }

    private fun Context.showSearchDialog() {
        scopeLife {
            initAllScopePreferences()

            val binding = DialogSearchResultLayoutBinding.inflate(layoutInflater)

            val dialog = MaterialAlertDialogBuilder(this@showSearchDialog).apply {
                setView(binding.root)
            }.show() ?: return@scopeLife

            val searchResultAdapter = SearchResultAdapter(
                allFragmentItem, object : OnSelectSearchResultListener {
                    override fun resultItem(fragmentItem: FragmentItem, prefsItem: PrefsItem) {
                        dialog.dismiss()
                        if (prefsItem.fragmentId == -1) return
                        prefsItem.fragmentId?.let { _ ->
                            val bundle = Bundle().apply {
                                putCharSequence("title_text", "Search Result")
                                putString("scrollKey", prefsItem.key)
                                putInt("scrollPosition", prefsItem.position)
                            }
                            navigatePage(prefsItem.fragmentId, bundle)
                        }
                    }
                })

            binding.searchViewLayout.apply {
                hint = "Title / Summary"
            }

            binding.searchView.apply {
                addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                    searchResultAdapter.getFilter.filter(text)
                })
            }

            binding.recyclerView.apply {
                adapter = searchResultAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }
        }
    }

    @Obfuscate
    class SearchResultAdapter(
        allFragmentItem: ArrayList<FragmentItem>,
        private val onSelectSearchResultListener: OnSelectSearchResultListener
    ) : RecyclerView.Adapter<SearchResultAdapter.SearchResultItemHolder>() {

        private var allFragmentItemDatas = ArrayMap<Int, FragmentItem>()
        private var allPrefsItemDatas = ArrayMap<Int, ArrayList<PrefsItem>>()

        private var allDatas = ArrayList<PrefsItem>()
        private var filterDatas = ArrayList<PrefsItem>()

        init {
            allFragmentItemDatas.clear()
            allPrefsItemDatas.clear()
            allDatas.clear()
            filterDatas.clear()

            allFragmentItem.forEachIndexed { _, fragmentItem ->
                allFragmentItemDatas[fragmentItem.fragmentId] = fragmentItem
                allPrefsItemDatas[fragmentItem.fragmentId] = fragmentItem.allPrefsItem
                allDatas.addAll(fragmentItem.allPrefsItem)
            }
//            LogUtils.d("SearchResultAdapter", "init", allDatas.size.toString(), true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemHolder {
            val binding = LayoutSearchResultItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SearchResultItemHolder(binding)
        }

        override fun onBindViewHolder(holder: SearchResultItemHolder, position: Int) {
            val prefsItem = filterDatas[position]
            val icon = prefsItem.icon
            val title = prefsItem.title
            val summary = prefsItem.summary
            val fragmentId = prefsItem.fragmentId
            val fragmentItem = allFragmentItemDatas[fragmentId]!!

            holder.item.setOnClickListener(null)
            holder.itemIcon.setImageDrawable(null)
            holder.itemTitle.text = null
            holder.itemSummary.text = null

            holder.item.setOnClickListener {
                onSelectSearchResultListener.resultItem(fragmentItem, prefsItem)
            }
            if (icon == null) holder.itemIcon.isVisible = false
            else holder.itemIcon.setImageDrawable(icon)
            holder.itemTitle.text = title
            if (summary.isNullOrBlank()) holder.itemSummary.isVisible = false
            else holder.itemSummary.text = summary
        }

        override fun getItemCount(): Int = filterDatas.size

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                filterDatas = if (constraint.isNotBlank()) {
                    ArrayList(allDatas.filter {
                        it.title?.contains(constraint, true) == true &&
                                it.isVisible == true && it.fragmentId != -1
                    })
                } else arrayListOf()
//                LogUtils.d("SearchResultAdapter", "getFilter", filterDatas.size.toString(), true)
                val filterResults = FilterResults()
                filterResults.values = filterDatas
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filterDatas = results.values as ArrayList<PrefsItem>
                refreshDatas()
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
        }

        @Obfuscate
        class SearchResultItemHolder(binding: LayoutSearchResultItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val item: ConstraintLayout = binding.root
            val itemIcon: ImageView = binding.itemIcon
            val itemTitle: TextView = binding.itemTitle
            val itemSummary: TextView = binding.itemSummary
        }
    }
}