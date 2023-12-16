package com.luckyzyx.luckytool.hook.scope.screenshot

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class CustomizeLongScreenshotMaxCapturedPages(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source ScrollCaptureConfigs -> scroll_configs_max_captured_pages / scroll_configs_max_captured_pixels
        //Source StitchLimitUtils -> isCapturedPagesReachLimit / trimToStitchLimit
        dexKitBridge.findClass {
            matcher {
                fieldCount(0)
                methods {
                    add { returnType(IntType) }
                    add { returnType(BooleanType) }
                    add {
                        paramTypes(IntType, IntType)
                        returnType(IntType)
                    }
                }
                usingStrings("StitchLimitUtils")
            }
        }.apply {
            checkDataList("CustomizeLongScreenshotMaxCapturedPages")
            single().name.toClass().apply {
                method {
                    param { it[1] == IntType }
                    paramCount = 2
                    returnType = BooleanType
                }.hook { replaceToFalse() }
                method {
                    param { it[1] == IntType && it[2] == IntType }
                    paramCount = 3
                    returnType = IntType
                }.hook { replaceTo(-1) }
            }
        }
    }
}