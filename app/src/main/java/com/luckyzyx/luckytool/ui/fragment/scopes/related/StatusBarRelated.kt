package com.luckyzyx.luckytool.ui.fragment.scopes.related

import android.content.Context
import android.util.ArraySet
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.listener.OnSelectAppInfoListener
import com.luckyzyx.luckytool.selector.AppInfoSelectDialog
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarBattery
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarClock
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarControlCenter
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarIcon
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarLayout
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarNetWorkSpeed
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarNotify
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarTiles
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.sendPrefsValue
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class StatusBarRelated : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf(
        "com.android.systemui",
        "com.oplus.battery",
        "com.coloros.phonemanager",
        "com.oplus.notificationmanager",
        "com.oplus.mediacontroller"
    )

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.statusBar

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
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
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(StatusBarClock().getRootPreference(this@loadPreferences))
            add(StatusBarNetWorkSpeed().getRootPreference(this@loadPreferences))
            add(StatusBarNotify().getRootPreference(this@loadPreferences))
            add(StatusBarIcon().getRootPreference(this@loadPreferences))
            add(StatusBarControlCenter().getRootPreference(this@loadPreferences))
            add(StatusBarTiles().getRootPreference(this@loadPreferences))
            add(StatusBarLayout().getRootPreference(this@loadPreferences))
            add(StatusBarBattery().getRootPreference(this@loadPreferences))
            //状态栏事件
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.StatusbarEvents)
                key = "StatusbarEvents"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.statusbar_double_click_lock_screen)
                key = "statusbar_double_click_lock_screen"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.vibrate_when_opening_the_statusbar)
                key = "vibrate_when_opening_the_statusbar"
                setDefaultValue(false)
                isVisible = osCode >= 26
                isIconSpaceReserved = false
            })
            add(DropDownPreference(this@loadPreferences).apply {
                title = getString(R.string.set_click_statusbar_scroll_to_top_mode)
                summary = arraySummaryLine(
                    getString(R.string.common_words_current_mode) + ": %s",
                    getString(R.string.need_restart_system)
                )
                key = "set_click_statusbar_scroll_to_top_mode"
                setEntries(R.array.set_click_statusbar_scroll_to_top_mode_entries)
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("0")
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            //音乐流体云
            if (osCode >= 33) {
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.custom_music_fluid_cloud_whitelist)
                    key = "custom_music_fluid_cloud_whitelist"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (getBoolean(ModulePrefs, "custom_music_fluid_cloud_whitelist")) {
                    add(SwitchPreference(this@loadPreferences).apply {
                        title = getString(R.string.disable_music_fluid_cloud_display)
                        key = "disable_music_fluid_cloud_display"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                    })
                    add(Preference(this@loadPreferences).apply {
                        title = getString(R.string.set_custom_music_fluid_cloud_whitelist)
                        key = "set_custom_music_fluid_cloud_whitelist"
                        val value = getStringSet(ModulePrefs, key, ArraySet())
                        summary = value.toString()
                        isEnabled = getBoolean(
                            ModulePrefs, "disable_music_fluid_cloud_display"
                        ).not()
                        isIconSpaceReserved = false
                        setOnPreferenceClickListener {
                            AppInfoSelectDialog(this@loadPreferences, true).apply {
                                setEnabledList(ArrayList(value))
                                setOnSelectAppListener(object : OnSelectAppInfoListener {
                                    override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                        val set = ArraySet<String>().apply {
                                            list.forEachIndexed { _, appInfo ->
                                                add(appInfo.packageName)
                                            }
                                        }
                                        putStringSet(ModulePrefs, key, set.toSet())
                                        (activity as MainActivity).restart()
                                    }
                                })
                                show()
                            }
                            true
                        }
                    })
                    add(SwitchPreference(this@loadPreferences).apply {
                        title = getString(R.string.disable_media_music_fluid_cloud_blacklist)
                        key = "disable_media_music_fluid_cloud_blacklist"
                        setDefaultValue(false)
                        isVisible = osCode >= 35
                        isIconSpaceReserved = false
                    })
                }
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.force_enable_media_music_fluid_cloud_ripple)
                    key = "force_enable_media_music_fluid_cloud_ripple"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        sendPrefsValue("com.oplus.mediacontroller", key, newValue)
                        true
                    }
                })
            }
        }
    }
}