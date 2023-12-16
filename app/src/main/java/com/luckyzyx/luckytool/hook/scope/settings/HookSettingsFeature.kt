package com.luckyzyx.luckytool.hook.scope.settings

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ApplicationInfoClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import org.luckypray.dexkit.DexKitBridge

class HookSettingsFeature(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        if (SDK < A13) loadHooker(HookExpUst(dexKitBridge))
    }

    class HookExpUst(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            val neverTimeout = prefs(ModulePrefs).getBoolean("enable_show_never_timeout", false)

            //Source ExpUstUtils
            dexKitBridge.findClass {
                matcher {
                    methods {
                        add { returnType(StringClass) }
                        add { returnType(BooleanType) }
                        add { returnType(ApplicationInfoClass) }
                        add { paramTypes(StringClass) }
                        add { paramTypes(IntType) }
                        add { paramTypes(IntType, StringClass) }
                        add { paramTypes(StringClass) }
                        add { paramTypes(StringClass, StringClass) }
                    }
                    usingStrings("screen_off_timeout")
                }
            }.apply {
                checkDataList("HookExpUst")
                single().name.toClass().apply {
                    method { param(IntType);returnType = BooleanType }.hookAll {
                        before {
                            when (args().first().int()) {
                                //Source DisplayTimeOutController -> 永不息屏(24H)
                                11 -> if (SDK < A13 && neverTimeout) resultTrue()
                            }
                        }
                    }
                }
            }
        }
    }
}