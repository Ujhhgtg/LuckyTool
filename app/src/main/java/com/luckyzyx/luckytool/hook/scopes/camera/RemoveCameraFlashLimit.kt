package com.luckyzyx.luckytool.hook.scopes.camera

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveCameraFlashLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookLowPowerFlashLimit(dexKitBridge))
    }

    @Obfuscate
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
                        paramTypes(IntType)
                        returnType(UnitType)
                        usingNumbers(15, 5, 2)
                    }
                }.apply {
                    checkDataList("RemoveCameraFlashLimit Method")
                    single().className.toClass().apply {
                        method {
                            name = single().methodName
                            param(IntType)
                            returnType = UnitType
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