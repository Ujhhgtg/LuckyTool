package com.luckyzyx.luckytool.hook.scopes.phonemanager

import android.os.CountDownTimer
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.method
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
            checkDataList("find clazz DialogCrossActivity")

            val clazz = single().name.toClass()
            if (!clazz.hasField { type = CountDownTimer::class.java }) return@apply

            findMethod {
                matcher {
                    paramCount(2..3)
                    addUsingField {
                        type(CountDownTimer::class.java)
                    }
                }
            }.apply {
                checkDataList("find method CountDownTimer", onlyOne = false)

                forEachIndexed { _: Int, methodData: MethodData ->
                    clazz.apply {
                        method {
                            name = methodData.methodName
                            paramCount(2..3)
                        }.hook {
                            intercept()
                        }
                    }
                }
            }
        }
    }
}