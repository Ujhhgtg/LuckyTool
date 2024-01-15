package com.luckyzyx.luckytool.hook.scopes.settings

import android.util.ArrayMap
import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.AtomicBooleanClass
import com.highcapable.yukihookapi.hook.type.java.InputStreamClass
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge
import java.io.Reader

class DarkModeList(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        var supportlistSet = prefs(ModulePrefs).getStringSet("dark_mode_support_list", ArraySet())
        dataChannel.wait<Set<String>>("dark_mode_support_list") { supportlistSet = it }

        //Source DarkModeFileUtils
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(AnyClass)
                    addForType(AtomicBooleanClass)
                    addForType(MapClass)
                }
                methods {
                    add { paramTypes(Reader::class.java) }
                    add { paramTypes(InputStreamClass) }
                }
                usingStrings("DarkModeFileUtils")
            }
        }.apply {
            checkDataList("DarkModeList")
            single().name.toClass().apply {
                val objectName = classes[0]?.simpleName
                val darkModeData = (canonicalName!! + "\$$objectName").toClass()
                method { param(Reader::class.java) }.hook {
                    replaceUnit {
                        val supportListMap = ArrayMap<String, Int>()
                        supportlistSet.forEach {
                            if (it.contains("|")) {
                                val arr = it.split("|").toMutableList()
                                if (arr.size < 2 || arr[1].isBlank()) arr[1] = (0).toString()
                                supportListMap[arr[0]] = arr[1].toInt()
                            } else supportListMap[it] = 0
                        }
                        val dataMap = ArrayMap<String, Any>()
                        supportListMap.forEach {
                            if (it.value == 0) dataMap[it.key] =
                                darkModeData.buildOf { emptyParam() }
                            else dataMap[it.key] = darkModeData.buildOf(0L, 0, it.value, 0) {
                                paramCount = 4
                            }
                        }
                        field { type = MapClass }.get().set(dataMap.toMap())
                    }
                }
            }
        }
    }
}