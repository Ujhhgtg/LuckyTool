package com.luckyzyx.luckytool.hook.scopes.screenshot

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BitmapClass
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.luckypray.dexkit.DexKitBridge

class RemoveScreenshotPrivacyLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 33) loadHooker(RemoveScreenshotPrivacyLimitV141(dexKitBridge))
        else loadHooker(RemoveScreenshotPrivacyLimitOld(dexKitBridge))
    }

    class RemoveScreenshotPrivacyLimitV141(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source TaskCapture
            dexKitBridge.findClass {
                matcher {
                    addFieldForType(StringClass)
                    addMethod {
                        paramTypes(
                            "com.oplus.app.OplusWindowInfo", null, "android.view.Display"
                        )
                        returnType(BitmapClass)
                    }
                    addMethod {
                        paramTypes(BitmapClass, null)
                        returnType(UnitType)
                    }
                    addMethod {
                        paramTypes("com.oplus.app.OplusWindowInfo")
                        returnType(UserHandleClass)
                    }
                    addMethod {
                        paramTypes("com.oplus.app.OplusWindowInfo")
                        returnType(BooleanType)
                    }
                }
            }.apply {
                checkDataList("RemoveScreenshotPrivacyLimitV141", isDebug = true)
                val dumpMethod = findMethod {
                    matcher {
                        paramTypes(null, IntType)
                        paramCount(2)
                        returnType(BooleanType)
                        usingStrings("Dump#screenshot")
                    }
                }.let {
                    it.checkDataList("RemoveScreenshotPrivacyLimitV141 Dump", isDebug = true)
                    it.single().methodName
                }
                val secureMethod = findMethod {
                    matcher {
                        paramCount(1)
                        returnType(BooleanType)
                        usingStrings("Secure#screenshot")
                    }
                }.let {
                    it.checkDataList("RemoveScreenshotPrivacyLimitV141 Secure", isDebug = true)
                    it.single().methodName
                }
                single().name.toClass().apply {
                    method { name = dumpMethod;returnType = BooleanType }.hook {
                        replaceToFalse()
                    }
                    method { name = secureMethod;returnType = BooleanType }.hook {
                        replaceToFalse()
                    }
                }
            }
        }
    }

    class RemoveScreenshotPrivacyLimitOld(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source ScreenshotContext
            "com.oplus.screenshot.screenshot.core.ScreenshotContext".toClassOrNull()?.apply {
                val hasOverrideScreenshotReject = hasMethod { name = "setScreenshotReject" }.not()
                method {
                    name = "setScreenshotReject";superClass(hasOverrideScreenshotReject)
                }.hook {
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
                    addCaller {
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
}