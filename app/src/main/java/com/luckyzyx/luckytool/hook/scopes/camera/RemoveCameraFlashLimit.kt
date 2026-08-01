package com.luckyzyx.luckytool.hook.scopes.camera

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveCameraFlashLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookLowPowerFlashLimit(dexKitBridge))
    }

    class HookLowPowerFlashLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source CameraManager
            dexKitBridge.findClass {
                matcher {
                    className("com.oplus.camera.CameraManager")
                }
            }.apply {
                checkDataList("RemoveCameraFlashLimit Clazz")
                findMethod {
                    matcher {
                        paramTypes(Int::class.java)
                        returnType(Void.TYPE)
                        usingNumbers(15, 5, 2)
                    }
                }.apply {
                    checkDataList("RemoveCameraFlashLimit Method")
                    single().className.toClass().resolve().apply {
                        firstMethod {
                            name = single().methodName
                            parameters(Int::class)
                            returnType = Void.TYPE
                        }.hook {
                            before {
                                args().first().set(100)
                            }
                        }
                    }
                }
            }
        }
    }
}