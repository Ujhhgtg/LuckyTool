package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArrayMap
import android.util.ArraySet
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.luckyzyx.luckytool.data.DarkModeInfo
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.oplus.darkmode.OplusDarkModeData
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object DarkModeService : YukiBaseHooker() {

    var isEnable = false
    val list = ArraySet<String>()

    fun loadData() {
        isEnable = prefs(ModulePrefs).getBoolean("dark_mode_list_enable", false)
        dataChannel.wait<Boolean>("dark_mode_list_enable") {
            isEnable = it
            YLog.debug("update dark mode service configs status -> $it")
        }

        list.clear()
        list.addAll(prefs(ModulePrefs).getStringSet("dark_mode_support_list", ArraySet()))
        dataChannel.wait<Set<String>>("dark_mode_support_list") {
            list.clear()
            val new = prefs(ModulePrefs).getStringSet("dark_mode_support_list", ArraySet())
            list.addAll(new)
            YLog.debug("update dark mode service whitelist configs -> ${list.size} | ${new.size}")
        }
        YLog.debug("init dark mode service configs success")
    }

    override fun onHook() {
        loadData()

        //Source OplusDarkModeServiceManager
        "com.android.server.OplusDarkModeServiceManager".toClass().resolve().optional().apply {
            firstMethod {
                name { it.startsWith("updateList") }
                parameterCount = 1
            }.hook {
                after {
                    if (!isEnable) return@after
                    val enabledDarkMode = ArrayList<DarkModeInfo>()
                    list.forEach {
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
                    firstField { name = "mRusAppMap" }.of(instance).set(dataMap.toMap())
//                    field { name = "mOpenApp" }.get(instance).set(supportlist)
//                    field { name = "mClickApp" }.get(instance).set(supportlist)
                }
            }
        }
    }
}