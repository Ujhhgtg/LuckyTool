package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.View
import android.view.ViewGroup
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.hook.utils.sysui.MediaPlayerDataUtils
import com.luckyzyx.luckytool.hook.utils.sysui.QSFeatureOptionUtils
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.getScreenOrientation

@Obfuscate
object ControlCenterTiles : YukiBaseHooker() {
    var callback: ((key: String, value: String) -> Unit)? = null

    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("control_center_tile_enable", false)
        if (!isEnable) return
        if (SDK >= A13) loadHooker(ControlCenterTilesLayout)
        else loadHooker(ControlCenterTilesLayoutC12)
    }

    @Obfuscate
    object ControlCenterTilesLayout : YukiBaseHooker() {
        override fun onHook() {
            val osCode = getOSVersionCode

            val columnUnexpandedVerticalC13 =
                prefs(ModulePrefs).getInt("tile_unexpanded_columns_vertical_c13", 5)
            val rowExpandedVerticalC13 =
                prefs(ModulePrefs).getInt("tile_expanded_rows_vertical_c13", 3)
            val columnExpandedVerticalC13 =
                prefs(ModulePrefs).getInt("tile_expanded_columns_vertical_c13", 4)
            val columnHorizontal = prefs(ModulePrefs).getInt("tile_columns_horizontal_c13", 4)
            //媒体播放器模式
            var mediaMode = prefs(ModulePrefs).getString("set_media_player_display_mode", "0")
            //自动扩展
            var autoExpandTile = osCode >= 30 && prefs(ModulePrefs).getBoolean(
                "auto_expand_tile_rows_horizontal", false
            )
            dataChannel.wait<Boolean>("auto_expand_tile_rows_horizontal") { autoExpandTile = it }

            callback = { key: String, value: String ->
                when (key) {
                    "set_media_player_display_mode" -> mediaMode = value
                }
            }

            //Source QuickQSPanel
            "com.android.systemui.qs.QuickQSPanel".toClass().apply {
                method { name = "getNumQuickTiles" }.hook {
                    replaceTo(columnUnexpandedVerticalC13)
                }
            }

            //Source TileLayout
            "com.android.systemui.qs.TileLayout".toClass().apply {
                val hasUpdateColumns = hasMethod { name = "updateColumns" }
                method { name = "updateMaxRows" }.hook {
                    before {
                        getScreenOrientation(instance<ViewGroup>()) {
                            val mRows = field { name = "mRows" }.get(instance).int()
                            val newRows = if (it) rowExpandedVerticalC13
                            else if (autoExpandTile &&
                                QSFeatureOptionUtils(appClassLoader).isSupportVolumeSeekBar().not()
                            ) when (mediaMode) {
                                "2" -> 2
                                "3" -> {
                                    if (MediaPlayerDataUtils(appClassLoader).checkMediaDataStatus() == null) 2
                                    else return@getScreenOrientation
                                }

                                else -> return@getScreenOrientation
                            }
                            else return@getScreenOrientation
                            field { name = "mRows" }.get(instance).set(newRows)
                            result = mRows != newRows
                        }
                    }
                }
                method {
                    name = if (hasUpdateColumns) "updateColumns" else "setMaxColumns"
                }.hook {
                    before {
                        instance<ViewGroup>().apply {
                            getScreenOrientation(this) {
                                val mColumns = field { name = "mColumns" }.get(instance).int()
                                val newColumns = if (it) columnExpandedVerticalC13
                                else columnHorizontal
                                field { name = "mColumns" }.get(instance).set(newColumns)
                                result = mColumns != newColumns
                            }
                        }
                    }
                }
            }
        }
    }

    @Obfuscate
    object ControlCenterTilesLayoutC12 : YukiBaseHooker() {
        override fun onHook() {
            val columnUnexpandedVertical =
                prefs(ModulePrefs).getInt("tile_unexpanded_columns_vertical", 6)
            val columnUnexpandedHorizontal =
                prefs(ModulePrefs).getInt("tile_unexpanded_columns_horizontal", 6)
            val columnExpandedVertical =
                prefs(ModulePrefs).getInt("tile_expanded_columns_vertical", 4)
            val columnExpandedHorizontal =
                prefs(ModulePrefs).getInt("tile_expanded_columns_horizontal", 6)

            //Source QuickQSPanel
            "com.android.systemui.qs.QuickQSPanel".toClass().apply {
                method { name = "getNumQuickTiles" }.hook {
                    before {
                        getScreenOrientation(instance<View>()) {
                            result = if (it) columnUnexpandedVertical
                            else columnUnexpandedHorizontal
                        }
                    }
                }
            }

            //Source TileLayout
            "com.android.systemui.qs.TileLayout".toClass().apply {
                method { name = "updateColumns" }.hook {
                    before {
                        instance<ViewGroup>().apply {
                            getScreenOrientation(this) {
                                val mColumns = field { name = "mColumns" }.get(instance).int()
                                val newColumns = if (it) columnExpandedVertical
                                else columnExpandedHorizontal
                                field { name = "mColumns" }.get(instance).set(newColumns)
                                result = mColumns != newColumns
                            }
                        }
                    }
                }
            }
        }
    }
}

