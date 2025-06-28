package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.ArrayClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object LauncherLayoutRowColume : YukiBaseHooker() {
    override fun onHook() {
        //Source UiConfig
        "com.android.launcher.UiConfig".toClass().resolve().apply {
            firstMethod { name = "isSupportLayout" }.hook {
                replaceToTrue()
            }
        }
        val maxRows = prefs(ModulePrefs).getInt("launcher_layout_max_rows", 6)
        val maxColumns = prefs(ModulePrefs).getInt("launcher_layout_max_columns", 4)
        //Source ToggleBarLayoutAdapter
        "com.android.launcher.togglebar.adapter.ToggleBarLayoutAdapter".toClass().resolve().apply {
            firstMethod { name = "initToggleBarLayoutConfigs" }.hook {
                before {
                    firstField {
                        name = "MIN_MAX_COLUMN"
                        type = ArrayClass(Int::class)
                    }.get<IntArray>()?.set(1, maxColumns)
                    firstField {
                        name = "MIN_MAX_ROW"
                        type = ArrayClass(Int::class)
                    }.get<IntArray>()?.set(1, maxRows)
                }
            }
        }
    }
}