package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import android.util.ArraySet
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.listener.OnSelectAppInfoListener
import com.luckyzyx.luckytool.selector.AppInfoSelector
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.putStringSet

@Obfuscate
class StatusBar : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf(
        "com.android.systemui",
        "com.oplus.battery",
        "com.coloros.phonemanager",
        "com.oplus.notificationmanager",
        "com.oplus.mediacontroller"
    )

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(Preference(context).apply {
                title = getString(R.string.StatusBarClock)
                summary = arraySummaryDot(
                    getString(R.string.statusbar_clock_show_second),
                    getString(R.string.statusbar_clock_show_doublerow),
                    getString(
                        R.string.statusbar_clock_doublerow_fontsize
                    )
                )
                key = "StatusBarClock"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBar_to_statusBarClock, title)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.StatusBarNetWorkSpeed)
                summary = arraySummaryDot(
                    getString(R.string.enable_double_row_network_speed),
                    getString(R.string.set_network_speed)
                )
                key = "StatusBarNetWorkSpeed"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBar_to_statusBarNetWorkSpeed, title)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.StatusBarNotice)
                summary = arraySummaryDot(
                    getString(R.string.RemoveStatusBarNotifications),
                    getString(R.string.remove_notification_manager_limit)
                )
                key = "StatusBarNotice"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBar_to_statusBarNotice, title)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.StatusBarIcon)
                summary = arraySummaryDot(
                    getString(R.string.remove_mobile_data_inout),
                    getString(R.string.remove_green_dot_privacy_prompt)
                )
                key = "StatusBarIcon"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBar_to_statusBarIcon, title)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.StatusBarControlCenter)
                summary = arraySummaryDot(
                    getString(R.string.control_center_clock_show_second),
                    getString(R.string.remove_control_center_clock_red_one)
                )
                key = "StatusBarControlCenter"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBar_to_statusBarControlCenter, title)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.StatusBarTiles)
                summary = arraySummaryDot(
                    getString(R.string.long_press_wifi_tile_open_the_page),
                    getString(R.string.fix_tile_align_both_sides)
                )
                key = "StatusBarTiles"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBar_to_statusBarTiles, title)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.StatusBarLayout)
                summary = arraySummaryDot(
                    getString(R.string.statusbar_layout_mode),
                    getString(R.string.statusbar_layout_compatible_mode)
                )
                key = "StatusBarLayout"
                isIconSpaceReserved = false
                isVisible = SDK == A13
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBar_to_statusBarLayout, title)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.StatusBarBattery)
                summary = arraySummaryDot(
                    getString(R.string.remove_statusbar_battery_percent),
                    getString(R.string.use_user_typeface)
                )
                key = "StatusBarBattery"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBar_to_statusBarBattery, title)
                    true
                }
            })
            //状态栏事件
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.StatusbarEvents)
                key = "StatusbarEvents"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.statusbar_double_click_lock_screen)
                key = "statusbar_double_click_lock_screen"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.vibrate_when_opening_the_statusbar)
                key = "vibrate_when_opening_the_statusbar"
                setDefaultValue(false)
                isVisible = osCode >= 26
                isIconSpaceReserved = false
            })
            addPreference(DropDownPreference(context).apply {
                title = getString(R.string.set_click_statusbar_scroll_to_top_mode)
                summary = arraySummaryLine(
                    getString(R.string.common_words_current_mode) + ": %s",
                    getString(R.string.need_restart_system)
                )
                key = "set_click_statusbar_scroll_to_top_mode"
                entries =
                    resources.getStringArray(R.array.set_click_statusbar_scroll_to_top_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            //音乐流体云
            if (osCode >= 33) {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.custom_music_fluid_cloud_whitelist)
                    key = "custom_music_fluid_cloud_whitelist"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (context.getBoolean(ModulePrefs, "custom_music_fluid_cloud_whitelist")) {
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.disable_music_fluid_cloud_display)
                        key = "disable_music_fluid_cloud_display"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                    })
                    addPreference(Preference(context).apply {
                        key = "set_custom_music_fluid_cloud_whitelist"
                        title = getString(R.string.set_custom_music_fluid_cloud_whitelist)
                        val value = context.getStringSet(ModulePrefs, key, ArraySet())
                        summary = value.toString()
                        isEnabled = context.getBoolean(
                            ModulePrefs, "disable_music_fluid_cloud_display"
                        ).not()
                        isIconSpaceReserved = false
                        setOnPreferenceClickListener {
                            AppInfoSelector(context, true).apply {
                                setEnabledList(ArrayList(value))
                                setOnSelectAppListener(object : OnSelectAppInfoListener {
                                    override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                        val set = ArraySet<String>().apply {
                                            list.forEachIndexed { _, appInfo ->
                                                add(appInfo.packageName)
                                            }
                                        }
                                        context.putStringSet(ModulePrefs, key, set.toSet())
                                        (activity as MainActivity).restart()
                                    }
                                })
                                show()
                            }
                            true
                        }
                    })
                }
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.force_enable_media_music_fluid_cloud_ripple)
                    key = "force_enable_media_music_fluid_cloud_ripple"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}