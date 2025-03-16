package com.luckyzyx.luckytool.ui.fragment.scopes.statusbar

import android.content.Context
import android.util.ArraySet
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.luckyzyx.commonutils.data.AppInfo
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.sendPrefsValue
import com.luckyzyx.selector.listener.OnSelectAppInfoListener
import com.luckyzyx.selector.selects.AppInfoSelector
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class StatusBarNotify : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf(
        "com.android.systemui",
        "com.oplus.battery",
        "com.coloros.phonemanager",
        "com.oplus.notificationmanager"
    )

    override val isEnableRestartMenu: Boolean = true

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.statusBarNotify

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            title = getString(R.string.StatusBarNotice)
            summary = arraySummaryDot(
                getString(R.string.RemoveStatusBarNotifications),
                getString(R.string.remove_notification_manager_limit)
            )
            key = "StatusBarNotice"
            isIconSpaceReserved = false
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        return ArrayList<Preference>().apply {
            add(StatusBarNotifyRemoval().getRootPreference(this@loadPreferences))
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.allow_long_press_notification_modifiable)
                key = "allow_long_press_notification_modifiable"
                setDefaultValue(false)
                isVisible = osCode <= 30
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_notification_manager_limit)
                key = "remove_notification_manager_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.disable_high_volume_warning_notifications)
                key = "disable_high_volume_warning_notifications"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            if (osCode < 34) {
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.remove_small_window_reply_whitelist)
                    key = "remove_small_window_reply_whitelist"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (getBoolean(ModulePrefs, "remove_small_window_reply_whitelist")) {
                    add(Preference(this@loadPreferences).apply {
                        key = "set_small_window_reply_blacklist_list"
                        title = getString(R.string.set_small_window_reply_blacklist)
                        val value = getStringSet(ModulePrefs, key, ArraySet())
                        summary = arraySummaryLine(
                            getString(R.string.set_small_window_reply_blacklist_message),
                            value.toString()
                        )
                        isIconSpaceReserved = false
                        setOnPreferenceClickListener {
                            AppInfoSelector(this@loadPreferences, true).apply {
                                setDefaultShowSystem(true)
                                setEnabledList(ArrayList(value))
                                setOnSelectAppListener(object : OnSelectAppInfoListener {
                                    override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                        val set = ArraySet<String>().apply {
                                            list.forEachIndexed { _, appInfo ->
                                                add(appInfo.packageName)
                                            }
                                        }
                                        putStringSet(ModulePrefs, key, set.toSet())
                                        sendPrefsValue("com.android.systemui", key, set.toSet())
                                        (activity as MainActivity).restart()
                                    }
                                })
                                show()
                            }
                            true
                        }
                    })
                }
            }
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_notification_pin_number_limit)
                key = "remove_notification_pin_number_limit"
                setDefaultValue(false)
                isVisible = osCode >= 33
                isIconSpaceReserved = false
            })
        }
    }
}