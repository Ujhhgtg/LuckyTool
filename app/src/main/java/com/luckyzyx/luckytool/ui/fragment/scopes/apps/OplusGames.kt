package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.content.Context
import android.content.Intent
import android.util.ArraySet
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.luckyzyx.commonutils.AppUtils
import com.luckyzyx.commonutils.data.AppInfo
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryDot
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.checkPackName
import com.luckyzyx.luckytool.utils.checkResolveActivity
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.setPrefsIconRes
import com.luckyzyx.selector.listener.OnSelectAppInfoListener
import com.luckyzyx.selector.selects.AppInfoSelector
import com.topjohnwu.superuser.ShellUtils
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class OplusGames : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.oplus.games", "com.oplus.cosa")

    override val isEnableRestartMenu: Boolean = true

    override val isEnableOpenMenu: Boolean
        get() {
            return if (activity == null) false
            else requireActivity().checkPackName("com.oplus.games") && requireActivity().checkResolveActivity(
                Intent().setClassName(
                    "com.oplus.games", "business.compact.activity.GameBoxCoverActivity"
                )
            )
        }

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = R.id.oplusGames

    override fun Context.loadRootPreference(): Preference {
        return Preference(this).apply {
            key = "com.oplus.games"
            setPrefsIconRes(key) { resource, show ->
                icon = resource
                isIconSpaceReserved = show
            }
            title = AppUtils(context).getAppLabel(key)
            summary = arraySummaryDot(
                getString(R.string.remove_root_check),
                getString(R.string.enable_developer_page)
            )
            isVisible = checkPackName(key)
        }
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        val appUtils = AppUtils(this@loadPreferences)
        val appVerInfo = appUtils.getAppVerInfo(scopes.first())
        val isNew = (AppUtils(this).getAppVersionName(scopes.first())
            ?.substringBefore(".")?.toIntOrNull() ?: 10) >= 10
        return ArrayList<Preference>().apply {
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.game_assistant_page)
                summary = "(${appUtils.getAppLabel("com.oplus.games")})"
                isVisible =
                    checkPackName("com.oplus.games") && checkResolveActivity(
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
            add(Preference(this@loadPreferences).apply {
                title = getString(R.string.game_space_page)
                summary = "(${appUtils.getAppLabel("com.nearme.gamecenter")})"
                isVisible =
                    checkPackName("com.nearme.gamecenter") && checkResolveActivity(
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
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.OplusGamesLayout)
                key = "OplusGamesLayout"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_startup_animation)
                key = "remove_startup_animation"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_welfare_page)
                key = "remove_welfare_page"
                setDefaultValue(false)
                isVisible = !isNew
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_tool_recommendation_card)
                key = "remove_tool_recommendation_card"
                setDefaultValue(false)
                isVisible = appVerInfo?.versionCode?.let { it < 90000000 } ?: false
                isIconSpaceReserved = false
            })
            //工具
            add(PreferenceCategory(this@loadPreferences).apply {
                title = getString(R.string.OplusGamesTool)
                key = "OplusGamesTool"
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_root_check)
                summary = getString(R.string.remove_root_check_summary)
                key = "remove_root_check"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_some_vip_limit)
                summary = getString(R.string.remove_some_vip_limit_summary)
                key = "remove_some_vip_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
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
            if (getBoolean(ModulePrefs, "enable_developer_page")) {
                add(Preference(this@loadPreferences).apply {
                    title = getString(R.string.game_assistant_develop_page)
                    summary = "(${appUtils.getAppLabel("com.oplus.games")})"
                    isVisible = checkPackName("com.oplus.games")
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        ShellUtils.fastCmd(
                            "am start -n com.oplus.games/business.compact.activity.GameDevelopOptionsActivity"
                        )
                        true
                    }
                })
            }
            add(Preference(this@loadPreferences).apply {
                key = "custom_media_player_support_list"
                title = getString(R.string.custom_media_player_support)
                val value = getStringSet(ModulePrefs, key, ArraySet())
                summary = value.toString()
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    AppInfoSelector(this@loadPreferences, true).apply {
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
            add(Preference(this@loadPreferences).apply {
                key = "custom_barrage_notification_whitelist_list"
                title = getString(R.string.custom_barrage_notification_whitelist)
                val value = getStringSet(ModulePrefs, key, ArraySet())
                summary = arraySummaryLine(
                    getString(R.string.custom_barrage_notification_whitelist_message),
                    value.toString()
                )
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    AppInfoSelector(this@loadPreferences, true).apply {
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
                title = getString(R.string.enable_run_in_background)
                key = "enable_game_run_in_background"
                setDefaultValue(false)
                isVisible = osCode >= 27
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_game_ai_play)
                key = "enable_game_ai_play"
                setDefaultValue(false)
                isVisible = appVerInfo?.versionCode?.let { it >= 90130000 } ?: false
                isVisible = !isNew
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_danmaku_notification_whitelist)
                key = "remove_danmaku_notification_whitelist"
                setDefaultValue(false)
                isVisible = SDK < A14
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_game_voice_changer_whitelist)
                key = "remove_game_voice_changer_whitelist"
                setDefaultValue(false)
                isVisible = !isNew
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_game_assistant_temperature_detection)
                summary = getString(R.string.remove_game_assistant_temperature_detection_summary)
                key = "remove_game_assistant_temperature_detection"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_support_competition_mode)
                key = "enable_support_competition_mode"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.remove_competition_mode_sound)
                key = "remove_competition_mode_sound"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            add(SwitchPreference(this@loadPreferences).apply {
                title = getString(R.string.enable_x_mode_feature)
                key = "enable_x_mode_feature"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            if (!isNew) {
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_gt_mode_feature)
                    key = "enable_gt_mode_feature"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_one_plus_characteristic)
                    key = "enable_one_plus_characteristic"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_adreno_gpu_controller)
                    key = "enable_adreno_gpu_controller"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_increase_fps_limit_feature)
                    key = "enable_increase_fps_limit_feature"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_increase_fps_feature)
                    key = "enable_increase_fps_feature"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_optimise_power_feature)
                    key = "enable_optimise_power_feature"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                add(SwitchPreference(this@loadPreferences).apply {
                    title = getString(R.string.enable_super_resolution_feature)
                    key = "enable_super_resolution_feature"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
        }
    }

    override fun callOpenMenu() {
        ShellUtils.fastCmd(
            "am start -n com.oplus.games/business.compact.activity.GameBoxCoverActivity"
        )
    }
}