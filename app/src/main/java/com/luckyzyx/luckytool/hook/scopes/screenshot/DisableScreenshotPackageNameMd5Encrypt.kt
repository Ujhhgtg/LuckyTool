package com.luckyzyx.luckytool.hook.scopes.screenshot

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class DisableScreenshotPackageNameMd5Encrypt(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source EncryptUtils
        dexKitBridge.findClass {
            matcher {
                addMethod { returnType(String::class.java) }
                usingStrings("EncryptUtils", "encryptToMd5", "queryEncryptName")
            }
        }.apply {
            checkDataList("DisableScreenshotPackageNameMd5Encrypt", isDebug = true)

            findMethod {
                matcher {
                    paramTypes(String::class.java)
                    returnType(String::class.java)
                    usingStrings("queryEncryptName")
                }
            }.apply {
                checkDataList("queryEncryptName")

                single().className.toClass().resolve().apply {
                    firstMethod {
                        name = single().methodName
                        parameters(String::class)
                        returnType = String::class
                    }.hook {
                        before {
                            val packName = args().first().string()
                            if (packName.isNotBlank()) result = packName
                        }
                    }
                }
            }
        }
    }
}