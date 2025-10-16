package com.luckyzyx.luckytool.hook.scopes.phonemanager

import android.os.CountDownTimer
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.result.MethodData

@Obfuscate
class RemoveCountdownAddVirusAppWhitelist(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        dexKitBridge.findClass {
            matcher {
                className("com.oplus.phonemanager.common.DialogCrossActivity")
            }
        }.apply {
            checkDataList("DialogCrossActivity")

            val resolver = single().name.toClass().resolve()
            resolver.firstFieldOrNull { type = CountDownTimer::class.java } ?: return

            findMethod {
                matcher {
                    paramCount(2..3)
                    addUsingField {
                        type(CountDownTimer::class.java)
                    }
                }
            }.apply {
                checkDataList("CountDownTimer", onlyOne = false)

                forEachIndexed { _: Int, methodData: MethodData ->
                    resolver.firstMethod {
                        name = methodData.methodName
                        parameterCount { it in 2..3 }
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}