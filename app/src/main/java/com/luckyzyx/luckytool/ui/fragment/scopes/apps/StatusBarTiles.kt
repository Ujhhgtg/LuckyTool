package com.luckyzyx.luckytool.ui.fragment.scopes.apps

import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceCategory
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.arraySummaryLine
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.sendPrefsValue

@Obfuscate
class StatusBarTiles : BaseScopePreferenceFeagment() {
    override val scopes = arrayOf("com.android.systemui")

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = ModulePrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            //静音或振动
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.force_display_of_ringing_status_toggle_tiles)
                key = "force_display_of_ringing_status_toggle_tiles"
                setDefaultValue(false)
                isIconSpaceReserved = false
            })
            //特殊磁贴
            if (SDK >= A13) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.SpecialTiles)
                    key = "SpecialTiles"
                    isIconSpaceReserved = false
                })
                addPreference(DropDownPreference(context).apply {
                    title = getString(R.string.set_media_player_display_mode)
                    summary = getString(R.string.common_words_current_mode) + ": %s"
                    key = "set_media_player_display_mode"
                    entries =
                        resources.getStringArray(R.array.set_media_player_display_mode_entries)
                    entryValues = arrayOf("0", "1", "2", "3")
                    setDefaultValue("0")
                    isIconSpaceReserved = false
                    setOnPreferenceChangeListener { _, newValue ->
                        context.sendPrefsValue("com.android.systemui", key, newValue)
                        (activity as MainActivity).restart()
                        true
                    }
                })
                if (context.getString(ModulePrefs, "set_media_player_display_mode", "0") != "0") {
                    if (SDK >= A14) addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.auto_expand_tile_rows_horizontal)
                        summary = arraySummaryLine(
                            getString(R.string.auto_expand_tile_rows_horizontal_summary),
                            getString(R.string.auto_expand_tile_rows_horizontal_summary_2)
                        )
                        key = "auto_expand_tile_rows_horizontal"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            context.sendPrefsValue("com.android.systemui", key, newValue)
                            if (newValue as Boolean) {
                                findPreference<SwitchPreference>("control_center_custom_gaps_for_special_tile")?.isChecked =
                                    true
                                findPreference<SwitchPreference>("control_center_tile_enable")?.isChecked =
                                    true
                            }
                            (activity as MainActivity).restart()
                            true
                        }
                    })
                }
                if (context.getString(ModulePrefs, "set_media_player_display_mode") == "1") {
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.force_enable_media_toggle_button)
                        key = "force_enable_media_toggle_button"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                    })
                }
                if (osCode >= 27) {
                    addPreference(SwitchPreference(context).apply {
                        title = getString(R.string.control_center_custom_gaps_for_special_tile)
                        if (context.getBoolean(ModulePrefs, "auto_expand_tile_rows_horizontal")) {
                            summary =
                                getString(R.string.control_center_custom_gaps_for_special_tile_summary)
                        }
                        key = "control_center_custom_gaps_for_special_tile"
                        setDefaultValue(false)
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            context.sendPrefsValue("com.android.systemui", key, newValue)
                            if ((newValue as Boolean).not()) findPreference<SwitchPreference>("auto_expand_tile_rows_horizontal")?.isChecked =
                                false
                            (activity as MainActivity).restart()
                            true
                        }
                    })
                    if (context.getBoolean(
                            ModulePrefs, "control_center_custom_gaps_for_special_tile", false
                        )
                    ) {
                        addPreference(SeekBarPreference(context).apply {
                            title = getString(R.string.control_center_special_tile_top_gap)
                            key = "control_center_special_tile_top_gap"
                            setDefaultValue(10)
                            max = 20
                            min = 0
                            showSeekBarValue = true
                            updatesContinuously = false
                            isIconSpaceReserved = false
                            setOnPreferenceChangeListener { _, newValue ->
                                context.sendPrefsValue("com.android.systemui", key, newValue)
                                true
                            }
                        })
                        addPreference(SeekBarPreference(context).apply {
                            title = getString(R.string.control_center_special_tile_bottom_gap)
                            key = "control_center_special_tile_bottom_gap"
                            setDefaultValue(0)
                            max = 20
                            min = 0
                            showSeekBarValue = true
                            updatesContinuously = false
                            isIconSpaceReserved = false
                            setOnPreferenceChangeListener { _, newValue ->
                                context.sendPrefsValue("com.android.systemui", key, newValue)
                                true
                            }
                        })
                    }
                }
            }
            if (SDK == A13) {
                //磁贴长按事件
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.TileLongClickEvent)
                    key = "TileLongClickEvent"
                    isIconSpaceReserved = false
                })
                addPreference(SwitchPreference(context).apply {
                    title = getString(R.string.restore_some_tile_long_press_event)
                    summary = getString(R.string.restore_some_tile_long_press_event_summary)
                    key = "restore_some_tile_long_press_event"
                    setDefaultValue(false)
                    isIconSpaceReserved = false
                })
            }
            //磁贴样式相关
            if (osCode >= 27) {
                addPreference(PreferenceCategory(context).apply {
                    title = getString(R.string.TileStyleRelated)
                    key = "TileStyleRelated"
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.custom_tile_background_transparency)
                    key = "custom_tile_background_transparency"
                    setDefaultValue(-1)
                    max = 10
                    min = -1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isIconSpaceReserved = false
                })
            }
            //磁贴布局相关
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.TileLayoutRelated)
                key = "TileLayoutRelated"
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.fix_tile_align_both_sides)
                summary = getString(R.string.fix_tile_align_both_sides_summary)
                key = "fix_tile_align_both_sides"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.restore_page_layout_row_count_for_edit_tiles)
                key = "restore_page_layout_row_count_for_edit_tiles"
                setDefaultValue(false)
                isVisible = SDK >= A13
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.control_center_tile_enable)
                key = "control_center_tile_enable"
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    if ((newValue as Boolean).not()) findPreference<SwitchPreference>("auto_expand_tile_rows_horizontal")?.isChecked =
                        false
                    (activity as MainActivity).restart()
                    true
                }
            })
            if (context.getBoolean(ModulePrefs, "control_center_tile_enable", false)) {
                //C12
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.tile_unexpanded_columns_vertical)
                    key = "tile_unexpanded_columns_vertical"
                    setDefaultValue(6)
                    max = 6
                    min = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isVisible = SDK < A13
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.tile_unexpanded_columns_horizontal)
                    key = "tile_unexpanded_columns_horizontal"
                    setDefaultValue(6)
                    max = 8
                    min = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isVisible = SDK < A13
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.tile_expanded_columns_vertical)
                    key = "tile_expanded_columns_vertical"
                    setDefaultValue(4)
                    max = 7
                    min = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isVisible = SDK < A13
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.tile_expanded_columns_horizontal)
                    key = "tile_expanded_columns_horizontal"
                    setDefaultValue(6)
                    max = 9
                    min = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isVisible = SDK < A13
                    isIconSpaceReserved = false
                })
                //C13+
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.tile_unexpanded_columns_vertical)
                    key = "tile_unexpanded_columns_vertical_c13"
                    setDefaultValue(5)
                    max = 6
                    min = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isVisible = SDK >= A13
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.tile_expanded_rows_vertical)
                    key = "tile_expanded_rows_vertical_c13"
                    setDefaultValue(3)
                    max = 6
                    min = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isVisible = SDK >= A13
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.tile_expanded_columns_vertical)
                    key = "tile_expanded_columns_vertical_c13"
                    setDefaultValue(4)
                    max = 7
                    min = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isVisible = SDK >= A13
                    isIconSpaceReserved = false
                })
                addPreference(SeekBarPreference(context).apply {
                    title = getString(R.string.tile_columns_horizontal_c13)
                    key = "tile_columns_horizontal_c13"
                    setDefaultValue(5)
                    max = 6
                    min = 1
                    showSeekBarValue = true
                    updatesContinuously = false
                    isVisible = SDK >= A13
                    isIconSpaceReserved = false
                })
            }
        }
    }

    override fun isEnableRestartMenu(): Boolean = true
}