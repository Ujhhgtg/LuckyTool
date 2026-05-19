package com.luckyzyx.luckytool.hook.scopes.mydevices

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class ForceEnableFeiniuCloudNasOption(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source FNOsUtils
        dexKitBridge.findClass {
            matcher {
                className("com.heytap.mydevices.core.device.feiniu.FNOsUtils")
            }
        }.apply {
            checkDataList("FNOsUtils")

            findMethod {
                matcher {
                    paramCount(0)
                    returnType(Boolean::class.java)
                    usingStrings("FNOsUtils", "isSupportedFeiNiuNas")
                }
            }.apply {
                checkDataList("isSupportedFeiNiuNas")

                single().className.toClass().resolve().apply {
                    firstMethod {
                        name = single().methodName
                        returnType = Boolean::class
                    }.hook {
                        replaceToTrue()
                    }
                }
            }
        }
    }
}