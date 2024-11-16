package com.luckyzyx.luckytool.hook.scopes.gallery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class HookFunctionManager(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //姜文电影滤镜
        val jangWen = prefs(ModulePrefs).getBoolean("enable_gallery_jiangwen_filter", false)

        //Source FunctionSwitchManager
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(MapClass)
                }
                methods {
                    add {
                        paramTypes(StringClass)
                        returnType(BooleanType)
                        usingStrings("FunctionSwitchManager", "getGroupName", "spKey")
                    }
                    add {
                        paramCount(1..5)
                        returnType(UnitType)
                    }
                }
                usingStrings("FunctionSwitchManager")
            }
        }.apply {
            checkDataList("HookFunctionManager")
            single().name.toClass().apply {
                method { param(StringClass);returnType(BooleanType) }.hook {
                    after {
                        when (args().first().string()) {
                            //姜文电影滤镜
                            "pref_jiangwen_filter_enable" -> if (jangWen) resultTrue()
                        }
                    }
                }
            }
        }
    }
}