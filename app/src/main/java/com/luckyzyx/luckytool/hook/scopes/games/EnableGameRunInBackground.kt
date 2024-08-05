package com.luckyzyx.luckytool.hook.scopes.games

import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.IntentUtils
import org.luckypray.dexkit.DexKitBridge

class EnableGameRunInBackground(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source HangUpUtil
        dexKitBridge.findClass {
            matcher {
                addFieldForType(ListClass)
                addMethod { paramCount(0);returnType(BooleanType) }
                addMethod { paramCount(0);returnType(UnitType) }
                addMethod { paramTypes(ContextClass);returnType(UnitType) }
                usingStrings("HangUpUtil", "isSupportBackgroundHangUp")
            }
        }.apply {
            checkDataList("EnableGameRunInBackground Cls")
            findMethod {
                matcher {
                    paramCount(0)
                    returnType(BooleanType)
                    usingStrings("isSupportBackgroundHangUp")
                }
            }.apply {
                checkDataList("EnableGameRunInBackground Support")
                single().className.toClass().apply {
                    method {
                        name = single().methodName
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        replaceToTrue()
                    }
                    method { param(ContextClass);returnType = UnitType }.hook {
                        replaceUnit {
                            val context = args().first().cast<Context>() ?: return@replaceUnit
                            IntentUtils(context).startBackgroundRunService()
                        }
                    }
                }
            }
        }
    }
}