package com.luckyzyx.luckytool.hook.scopes.gallery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveAIGCEliminationLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source EliminateDetectInfo
        dexKitBridge.findClass {
            matcher {
                addFieldForType(BooleanType)
                addMethod { name("equals") }
                addMethod { name("hashCode") }
                addMethod { name("toString") }
                usingStrings("EliminateDetectInfo", "isContentSensitive")
            }
        }.apply {
            checkDataList("EliminateDetectInfo")

            single().name.toClass().apply {
                constructor { paramCount = 2 }.hook {
                    after {
                        field { type = BooleanType }.get(instance).setFalse()
                    }
                }
            }
        }
    }
}