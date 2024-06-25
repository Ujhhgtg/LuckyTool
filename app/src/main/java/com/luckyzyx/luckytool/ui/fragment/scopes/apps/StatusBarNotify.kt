package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import android.util.ArraySet
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.listener.OnSelectAppInfoListener
import com.luckyzyx.luckytool.selector.AppInfoSelector
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class StatusBarNotify : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf(
        "com.android.systemui",
        "com.oplus.battery",
        "com.coloros.phonemanager",
        "com.oplus.notificationmanager"
    )

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(Preference(context).apply {
                title = getString(R.string.RemoveStatusBarNotifications)
                summary = arraySummaryDot(
                    getString(R.string.remove_statusbar_top_notification),
                    getString(R.string.remove_statusbar_devmode)
                )
                key = "RemoveStatusBarNotifications"
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_statusBarNotice_to_statusBarNotifyRemoval, title)
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.allow_long_press_notification_modifiable)
                key = "allow_long_press_notification_modifiable"
                setDefaultValue(false)
                isVisible = osCode <= 30
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_notification_manager_limit)
                key = "remove_notification_manager_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_small_window_reply_whitelist)
                key = "remove_small_window_reply_whitelist"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (context.getBoolean(ModulePrefs, "remove_small_window_reply_whitelist")) {
                addPreference(Preference(context).apply {
                    key = "set_small_window_reply_blacklist_list"
                    title = getString(R.string.set_small_window_reply_blacklist)
                    val value = context.getStringSet(ModulePrefs, key, ArraySet())
                    summary = arraySummaryLine(
                        getString(R.string.set_small_window_reply_blacklist_message),
                        value.toString()
                    )
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        AppInfoSelector(context, true).apply {
                            setDefaultShowSystem(true)
                            setEnabledList(ArrayList(value))
                            setOnSelectAppListener(object : OnSelectAppInfoListener {
                                override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                    val set = ArraySet<String>().apply {
                                        list.forEachIndexed { _, appInfo ->
                                            add(appInfo.packageName)
                                        }
                                    }
                                    context.putStringSet(ModulePrefs, key, set.toSet())
                                    context.sendPrefsValue("com.android.systemui", key, set.toSet())
                                    (activity as MainActivity).restart()
                                }
                            })
                            show()
                        }
                        true
                    }
                })
            }
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
                    addPreference(Preference(context).apply {
                        key = "set_custom_music_fluid_cloud_whitelist"
                        title = getString(R.string.set_custom_music_fluid_cloud_whitelist)
                        val value = context.getStringSet(ModulePrefs, key, ArraySet())
                        summary = value.toString()
                        isIconSpaceReserved = false
                        setOnPreferenceClickListener {
                            AppInfoSelector(context, true).apply {
                                setDefaultShowSystem(true)
                                setEnabledList(ArrayList(value))
                                setOnSelectAppListener(object : OnSelectAppInfoListener {
                                    override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                        val set = ArraySet<String>().apply {
                                            list.forEachIndexed { _, appInfo ->
                                                add(appInfo.packageName)
                                            }
                                        }
                                        context.putStringSet(ModulePrefs, key, set.toSet())
                                        context.sendPrefsValue(
                                            "com.android.systemui", key, set.toSet()
                                        )
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
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}