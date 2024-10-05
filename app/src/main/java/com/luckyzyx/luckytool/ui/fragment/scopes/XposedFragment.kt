package com.luckyzyx.luckytool.ui.fragment.scopes

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.collection.ArrayMap
import androidx.core.view.MenuProvider
import androidx.core.view.setPadding
import androidx.preference.Preference
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withMain
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogScopeVersionInfoBinding
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
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
import com.luckyzyx.luckytool.utils.restartMain
import com.luckyzyx.luckytool.utils.safeOf
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import com.luckyzyx.luckytool.utils.setupMenuProvider
import com.luckyzyx.luckytool.utils.showBottomSheet
import com.luckyzyx.luckytool.utils.showToast
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import kotlinx.coroutines.Dispatchers
import java.util.Arrays

@Obfuscate
class XposedFragment : BaseScopePreferenceFeagment(), MenuProvider {

    override val currentPrefsName: String = ModulePrefs

    private val allPrefsItem = ArrayMap<Int, ArrayList<PrefsItem>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {

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
                    navigatePage(R.id.android, title)
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.menu_reboot)).apply {
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
        if (menuItem.itemId == 1) (activity as MainActivity).restartMain()
        if (menuItem.itemId == 2) requireActivity().showBottomDialog()
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
}