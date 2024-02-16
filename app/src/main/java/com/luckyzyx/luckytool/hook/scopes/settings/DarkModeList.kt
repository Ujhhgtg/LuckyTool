package com.luckyzyx.luckytool.hook.scopes.settings

import android.util.ArrayMap
import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.AtomicBooleanClass
import com.highcapable.yukihookapi.hook.type.java.InputStreamClass
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.luckyzyx.luckytool.data.DarkModeInfo
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge
import java.io.Reader

class DarkModeList(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        var isEnable = prefs(ModulePrefs).getBoolean("dark_mode_list_enable", false)
        dataChannel.wait<Boolean>("dark_mode_list_enable") { isEnable = it }
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
                val appEntity = classes[0]?.name?.toClassOrNull() ?: return
                method { param(ContextClass, MapClass) }.hook {
                    after {
                        if (!isEnable) return@after
                        val enabledDarkMode = ArrayList<DarkModeInfo>()
                        supportlistSet.forEach {
                            val darkModeInfo = DarkModeInfo().toDarkModeInfo(it)
                            if (darkModeInfo != null) enabledDarkMode.add(darkModeInfo)
                        }
                        val dataMap = ArrayMap<String, Any>()
                        enabledDarkMode.forEach {
                            if (it.curType == 0) dataMap[it.packName] = appEntity.buildOf {
                                emptyParam()
                            }
                            else dataMap[it.packName] = appEntity.buildOf(0L, 0, it.curType, 0) {
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