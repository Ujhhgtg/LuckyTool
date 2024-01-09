package com.luckyzyx.luckytool.ui.fragment.others

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.checkResolveActivity
import com.luckyzyx.luckytool.utils.getAppLabel
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.jumpBatteryInfo
import com.luckyzyx.luckytool.utils.jumpEngineermode
import com.luckyzyx.luckytool.utils.jumpRunningApp
import com.luckyzyx.luckytool.utils.jumpSettingsDev
import com.luckyzyx.luckytool.utils.jumpSystemUIDemoMode
import com.topjohnwu.superuser.ShellUtils

@Obfuscate
class QuickEntryFragment : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            //系统调试相关
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.SystemDebuggingRelated)
                key = "SystemDebuggingRelated"
                isIconSpaceReserved = false
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.engineering_mode)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    jumpEngineermode(context)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.charging_test)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    jumpBatteryInfo(context)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.developer_option)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    jumpSettingsDev(context)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.system_interface_adjustment)
                isVisible = context.checkResolveActivity(
                    Intent().setClassName(
                        "com.android.systemui", "com.android.systemui.DemoMode"
                    )
                )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    jumpSystemUIDemoMode(context)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.AOSPSettingsPage)
                isVisible = context.checkResolveActivity(
                    Intent().setClassName(
                        "com.android.settings",
                        "com.android.settings.homepage.DeepLinkHomepageActivityInternal"
                    )
                )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    ShellUtils.fastCmd(
                        "am start -n com.android.settings/.homepage.DeepLinkHomepageActivityInternal"
                    )
                    true
                }
            })
            //隐藏页面相关
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.HidePageRelated)
                key = "HidePageRelated"
                isIconSpaceReserved = false
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.process_manager)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    jumpRunningApp(context)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.very_dark_mode)
                isVisible = context.checkResolveActivity(
                    Intent().setClassName(
                        "com.android.settings",
                        "com.android.settings.Settings\$ReduceBrightColorsSettingsActivity"
                    )
                )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    Intent("android.settings.REDUCE_BRIGHT_COLORS_SETTINGS").apply {
                        setPackage("com.android.settings")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        startActivity(this)
                    }
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.open_battery_health)
                isVisible = context.checkResolveActivity(
                    Intent().setClassName(
                        "com.oplus.battery", "com.oplus.powermanager.fuelgaue.BatteryHealthActivity"
                    )
                )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    ShellUtils.fastCmd(
                        "am start -n com.oplus.battery/com.oplus.powermanager.fuelgaue.BatteryHealthActivity"
                    )
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.battery_optimization)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        startActivity(this)
                    }
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.camera_algo_page)
                isVisible = context.checkResolveActivity(
                    Intent().setClassName(
                        "com.oplus.camera", "com.oplus.camera.ui.menu.algoswitch.AlgoSwitchActivity"
                    )
                )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    ShellUtils.fastCmd(
                        "am start -n com.oplus.camera/.ui.menu.algoswitch.AlgoSwitchActivity"
                    )
                    true
                }
            })
            //游戏助手相关
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.GameAssistantRelated)
                isVisible = context.checkPackName("com.oplus.games")
                isIconSpaceReserved = false
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.game_assistant_page)
                summary = "(${context.getAppLabel("com.oplus.games")})"
                isVisible = context.checkPackName("com.oplus.games") &&
                        context.checkResolveActivity(
                            Intent().setClassName(
                                "com.oplus.games",
                                "business.compact.activity.GameBoxCoverActivity"
                            )
                        )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    ShellUtils.fastCmd(
                        "am start -n com.oplus.games/business.compact.activity.GameBoxCoverActivity"
                    )
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.game_space_page)
                summary = "(${context.getAppLabel("com.nearme.gamecenter")})"
                isVisible =
                    context.checkPackName("com.nearme.gamecenter") && context.checkResolveActivity(
                        Intent().setClassName(
                            "com.nearme.gamecenter",
                            "com.nearme.gamespace.desktopspace.ui.DesktopSpaceMainActivity"
                        )
                    )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    ShellUtils.fastCmd(
                        "am start -n com.nearme.gamecenter/com.nearme.gamespace.desktopspace.ui.DesktopSpaceMainActivity"
                    )
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.game_assistant_develop_page)
                summary = "(${context.getAppLabel("com.oplus.games")})"
                isVisible = context.checkPackName("com.oplus.games") && context.getBoolean(
                    ModulePrefs, "enable_developer_page", false
                )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    ShellUtils.fastCmd(
                        "am start -n com.oplus.games/business.compact.activity.GameDevelopOptionsActivity"
                    )
                    true
                }
            })
        }
    }
}
