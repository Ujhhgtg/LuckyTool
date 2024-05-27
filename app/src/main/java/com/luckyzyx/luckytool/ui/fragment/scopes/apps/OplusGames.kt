package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Intent
import android.os.Bundle
import android.util.ArraySet
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
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.checkResolveActivity
import com.luckyzyx.luckytool.utils.getAppLabel
import com.luckyzyx.luckytool.utils.getAppVerInfo
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putStringSet
import com.topjohnwu.superuser.ShellUtils

@Obfuscate
class OplusGames : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.games", "com.oplus.cosa")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            val appVerInfo = context.getAppVerInfo(scopes.first())
            addPreference(Preference(context).apply {
                title = getString(R.string.game_assistant_page)
                summary = "(${context.getAppLabel("com.oplus.games")})"
                isVisible =
                    context.checkPackName("com.oplus.games") && context.checkResolveActivity(
                        Intent().setClassName(
                            "com.oplus.games", "business.compact.activity.GameBoxCoverActivity"
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
            //布局
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.OplusGamesLayout)
                key = "OplusGamesLayout"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_startup_animation)
                key = "remove_startup_animation"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_welfare_page)
                key = "remove_welfare_page"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_tool_recommendation_card)
                key = "remove_tool_recommendation_card"
                setDefaultValue(false)
                isVisible = appVerInfo?.versionCode?.let { it < 90000000 } ?: false
                isIconSpaceReserved = false
            })
            //工具
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.OplusGamesTool)
                key = "OplusGamesTool"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_root_check)
                summary = getString(R.string.remove_root_check_summary)
                key = "remove_root_check"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_some_vip_limit)
                summary = getString(R.string.remove_some_vip_limit_summary)
                key = "remove_some_vip_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_developer_page)
                summary = getString(R.string.enable_developer_page_summary)
                key = "enable_developer_page"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (context.getBoolean(ModulePrefs, "enable_developer_page")) {
                addPreference(Preference(context).apply {
                    title = getString(R.string.game_assistant_develop_page)
                    summary = "(${context.getAppLabel("com.oplus.games")})"
                    isVisible = context.checkPackName("com.oplus.games")
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        ShellUtils.fastCmd(
                            "am start -n com.oplus.games/business.compact.activity.GameDevelopOptionsActivity"
                        )
                        true
                    }
                })
            }
            addPreference(Preference(context).apply {
                key = "custom_media_player_support_list"
                title = getString(R.string.custom_media_player_support)
                val value = context.getStringSet(ModulePrefs, key, ArraySet())
                summary = value.toString()
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
            addPreference(Preference(context).apply {
                key = "custom_barrage_notification_whitelist_list"
                title = getString(R.string.custom_barrage_notification_whitelist)
                val value = context.getStringSet(ModulePrefs, key, ArraySet())
                summary = arraySummaryLine(
                    getString(R.string.custom_barrage_notification_whitelist_message),
                    value.toString()
                )
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
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_run_in_background)
                key = "enable_game_run_in_background"
                setDefaultValue(false)
                isVisible = osCode >= 27
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_danmaku_notification_whitelist)
                key = "remove_danmaku_notification_whitelist"
                setDefaultValue(false)
                isVisible = SDK < A14
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_game_voice_changer_whitelist)
                key = "remove_game_voice_changer_whitelist"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_game_assistant_temperature_detection)
                summary = getString(R.string.remove_game_assistant_temperature_detection_summary)
                key = "remove_game_assistant_temperature_detection"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_x_mode_feature)
                key = "enable_x_mode_feature"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_gt_mode_feature)
                key = "enable_gt_mode_feature"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_one_plus_characteristic)
                key = "enable_one_plus_characteristic"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_adreno_gpu_controller)
                key = "enable_adreno_gpu_controller"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_support_competition_mode)
                key = "enable_support_competition_mode"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_competition_mode_sound)
                key = "remove_competition_mode_sound"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_increase_fps_limit_feature)
                key = "enable_increase_fps_limit_feature"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_increase_fps_feature)
                key = "enable_increase_fps_feature"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_optimise_power_feature)
                key = "enable_optimise_power_feature"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_super_resolution_feature)
                key = "enable_super_resolution_feature"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
    override fun isEnableOpenMenu(): Boolean =
        requireActivity().checkPackName("com.oplus.games") && requireActivity().checkResolveActivity(
            Intent().setClassName(
                "com.oplus.games", "business.compact.activity.GameBoxCoverActivity"
            )
        )

    override fun callOpenMenu() {
        ShellUtils.fastCmd(
            "am start -n com.oplus.games/business.compact.activity.GameBoxCoverActivity"
        )
    }
}