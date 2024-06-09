package com.luckyzyx.luckytool.hook.scopes.screenshot

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveScreenshotPrivacyLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source ScreenshotContext
        "com.oplus.screenshot.screenshot.core.ScreenshotContext".toClass().apply {
            val hasOverrideScreenshotReject = hasMethod { name = "setScreenshotReject" }.not()
            method { name = "setScreenshotReject";superClass(hasOverrideScreenshotReject) }.hook {
                intercept()
            }
            val hasOverrideLongshotReject = hasMethod { name = "setLongshotReject" }.not()
            method { name = "setLongshotReject";superClass(hasOverrideLongshotReject) }.hook {
                intercept()
            }
        }

        //Source ScreenshotRejectsManager
        dexKitBridge.findMethod {
            matcher {
                paramTypes(null, BooleanType, BundleClass)
                returnType(BooleanType)
                declaredClass {
                    usingStrings("ScreenshotRejectsManager")
                }
                addCall {
                    paramTypes(BundleClass)
                    usingStrings("loadScreenshotReject")
                }
            }
        }.apply {
            checkDataList("RemoveScreenshotPrivacyLimit")
            single().className.toClass().apply {
                method {
                    name = single().methodName
                    param(VagueType, BooleanType, BundleClass)
                    returnType = BooleanType
                }.hook {
                    after {
                        val type = args().first().any() ?: return@after
                        val res = result<Boolean>() ?: return@after
//                        YLog.debug("enum -> $type -> $res")
                        if (type.toString().contains("SECURE_WINDOW") && res) resultFalse()
                    }
                }
            }
        }
    }
}