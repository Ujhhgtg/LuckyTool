package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.FileUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.openApp
import com.luckyzyx.luckytool.utils.putString

@Obfuscate
class OplusSettings : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf(
        "com.android.settings",
        "com.android.permissioncontroller",
        "com.oplus.safecenter",
        "com.oplus.notificationmanager"
    )
    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            val cacheFile = FileUtils.getMSMCacheFile(requireActivity(), it)
            requireActivity().putString(
                ModulePrefs, "customize_device_ota_card_background_path", cacheFile?.path ?: ""
            )
        }
        (activity as MainActivity).restart()
    }

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            //连接与共享
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_connection_sharing)
                key = "settings_connection_sharing"
                isVisible = false
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.force_display_multi_screen_connect)
                key = "force_display_multi_screen_connect"
                setDefaultValue(false)
                isVisible = false
                isIconSpaceReserved = false
            })
            //状态栏
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_status_bar)
                key = "settings_status_bar"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_statusbar_clock_format)
                summary = getString(R.string.enable_statusbar_clock_format_summary)
                key = "enable_statusbar_clock_format"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //锁屏
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_lock_screen)
                key = "settings_lock_screen"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_show_never_timeout)
                summary = getString(R.string.enable_show_never_timeout_summary)
                key = "enable_show_never_timeout"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //显示
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_display)
                key = "settings_display"
                isIconSpaceReserved = false
            })
            if (osCode >= 30) {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_extra_brightness)
                    summary = arraySummaryLine(
                        getString(R.string.need_restart_system)
                    )
                    key = "enable_extra_brightness"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_lowest_allowed_brightness)
                    key = "enable_lowest_allowed_brightness"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            if (osCode >= 26) {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_video_memc_frame_insertion)
                    summary = arraySummaryLine(
                        getString(R.string.enable_video_memc_frame_insertion_summary),
                        getString(R.string.need_restart_system)
                    )
                    key = "enable_video_memc_frame_insertion"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (context.getBoolean(ModulePrefs, "enable_video_memc_frame_insertion", false)) {
                    addPreference(Preference(context).apply {
                        title =
                            getString(R.string.custom_video_dynamic_frame_insertion_configuration)
                        summary = arraySummaryLine(
                            getString(R.string.need_restart_system)
                        )
                        key = "custom_video_dynamic_frame_insertion_configuration"
                        isIconSpaceReserved = false
                        setOnPreferenceClickListener {
                            navigatePage(R.id.action_settings_to_memcConfigFragment, title)
                            true
                        }
                    })
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.video_frame_insertion_support_2K120)
                        summary = getString(R.string.video_frame_insertion_support_2K120_summary)
                        key = "video_frame_insertion_support_2K120"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                    })
                }
            }
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_screen_color_temperature_rgb_palette)
                summary = arraySummaryLine(
                    getString(R.string.need_restart_system),
                    getString(R.string.enable_screen_color_temperature_rgb_palette_summary)
                )
                key = "enable_screen_color_temperature_rgb_palette"
                setDefaultValue(false)
                isVisible = osCode >= 27
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_smart_switching_screen_resolutions)
                key = "enable_smart_switching_screen_resolutions"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //声音
            if (osCode >= 27) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.settings_sound)
                    key = "settings_sound"
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_clear_voice)
                    summary = arraySummaryLine(
                        getString(R.string.enable_clear_voice_tips),
                        getString(R.string.need_restart_system)
                    )
                    key = "enable_clear_voice"
                    setDefaultValue(false)
                    isVisible = SDK >= A14
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_holographic_audio)
                    key = "enable_holographic_audio"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            //应用
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_application)
                key = "settings_application"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.unlock_default_desktop_limit)
                key = "unlock_default_desktop_limit"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.force_display_process_management)
                key = "force_display_process_management"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.force_display_disabled_apps_manager)
                key = "force_display_disabled_apps_manager"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.auto_unlock_restricted_settings)
                summary = getString(R.string.auto_unlock_restricted_settings_summary)
                key = "auto_unlock_restricted_settings"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_dedicated_ram_for_games)
                key = "enable_dedicated_ram_for_games"
                setDefaultValue(false)
                isVisible = osCode >= 27
                isIconSpaceReserved = false
            })
            //密码与安全
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_password_and_security)
                key = "settings_password_and_security"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_google_auto_fill)
                key = "enable_google_auto_fill"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.restore_password_management_settings)
                key = "force_display_password_management_settings"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.disable_device_admin_verification_dialog)
                key = "disable_device_admin_verification_dialog"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //权限与隐私
            if (SDK >= 666) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.settings_authority_and_privacy)
                    key = "settings_authority_and_privacy"
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.enable_vip_mode)
                    key = "enable_vip_mode"
                    setDefaultValue(false)
                    isVisible = false
                    isIconSpaceReserved = false
                })
            }
            //其他设置
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_other_advanced_settings)
                key = "settings_other_advanced_settings"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_touch_membrane_protector_mode)
                key = "enable_touch_membrane_protector_mode"
                setDefaultValue(false)
                isVisible = osCode >= 30
                isIconSpaceReserved = false
            })
            if (osCode >= 30) {
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.disable_otg_auto_off)
                    summary = getString(R.string.disable_otg_auto_off_summary)
                    key = "disable_otg_auto_off"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.enable_game_acceleration)
                summary = arraySummaryLine(
                    getString(R.string.need_restart_system)
                )
                key = "enable_game_acceleration"
                setDefaultValue(false)
                isVisible = osCode >= 30
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.force_display_content_recommend)
                key = "force_display_content_recommend"
                setDefaultValue(false)
                isVisible = isZh(context)
                isIconSpaceReserved = false
            })
            //关于本机
            if (SDK >= A13) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.settings_about_device)
                    key = "settings_about_device"
                    isIconSpaceReserved = false
                })
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.set_processor_click_page)
                    summary = "%s"
                    key = "set_processor_click_page"
                    setEntries(R.array.set_processor_click_page_entries)
                    entryValues = arrayOf("0", "1", "2")
                    setDefaultValue("0")
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.screen_physics_size_shown_cm)
                    key = "screen_physics_size_shown_cm"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.customize_device_sharing_page_parameters)
                    key = "customize_device_sharing_page_parameters"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.customize_device_ota_card_background)
                    summary = getString(R.string.customize_device_ota_card_background_summary)
                    key = "customize_device_ota_card_background"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, _ ->
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (context.getBoolean(
                        ModulePrefs, "customize_device_ota_card_background", false
                    )
                ) {
                    addPreference(Preference(context).apply {
                        title = getString(R.string.customize_device_ota_card_background_path)
                        key = "customize_device_ota_card_background_path"
                        val path = context.getString(ModulePrefs, key, "")
                        if (path.isBlank()) {
                            summary = "Null"
                            isIconSpaceReserved = false
                        } else {
                            icon = BitmapFactory.decodeFile(path)?.toDrawable(context.resources)
                            summary = path
                            isCopyingEnabled = true
                        }
                        setOnPreferenceClickListener {
                            pickMedia.launch("image/*")
                            true
                        }
                    })
                }
            }
            //其他首选项
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_other_preference)
                key = "settings_other_preference"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_top_account_display)
                key = "remove_top_account_display"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.disable_cn_special_edition_setting)
                key = "disable_cn_special_edition_setting"
                setDefaultValue(false)
                isVisible = isZh(context)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_settings_bottom_laboratory)
                key = "remove_settings_bottom_laboratory"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.force_display_bottom_google_settings)
                key = "force_display_bottom_google_settings"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //开发者选项
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.settings_developer_preference)
                key = "settings_developer_preference"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.remove_dpi_restart_recovery)
                summary = getString(R.string.remove_dpi_restart_recovery_summary)
                key = "remove_dpi_restart_recovery"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
    override fun isEnableOpenMenu(): Boolean = true
    override fun callOpenMenu() = requireActivity().openApp(scopes)
}