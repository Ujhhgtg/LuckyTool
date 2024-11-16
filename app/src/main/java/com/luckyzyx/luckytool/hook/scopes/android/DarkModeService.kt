package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArrayMap
import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.data.DarkModeInfo
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.oplus.darkmode.OplusDarkModeData

@Obfuscate
object DarkModeService : YukiBaseHooker() {
    override fun onHook() {
        var isEnable = prefs(ModulePrefs).getBoolean("dark_mode_list_enable", false)
        dataChannel.wait<Boolean>("dark_mode_list_enable") { isEnable = it }
        var supportlistSet = prefs(ModulePrefs).getStringSet("dark_mode_support_list", ArraySet())
        dataChannel.wait<Set<String>>("dark_mode_support_list") { supportlistSet = it }

        //Source OplusDarkModeServiceManager
        "com.android.server.OplusDarkModeServiceManager".toClass().apply {
            method {
                name { it.startsWith("updateList") }
                paramCount = 1
            }.hook {
                after {
                    if (!isEnable) return@after
                    val enabledDarkMode = ArrayList<DarkModeInfo>()
                    supportlistSet.forEach {
                        val darkModeInfo = DarkModeInfo().toDarkModeInfo(it)
                        if (darkModeInfo != null) enabledDarkMode.add(darkModeInfo)
                    }
                    val dataMap = ArrayMap<String, OplusDarkModeData>()
                    enabledDarkMode.forEach {
                        if (it.curType == 0) dataMap[it.packName] = OplusDarkModeData()
                        else dataMap[it.packName] = OplusDarkModeData().apply {
                            mCurType = it.curType
                        }
                    }
                    field { name = "mRusAppMap" }.get(instance).set(dataMap.toMap())
//                    field { name = "mOpenApp" }.get(instance).set(supportlist)
//                    field { name = "mClickApp" }.get(instance).set(supportlist)
                }
            }
        }
    }
}