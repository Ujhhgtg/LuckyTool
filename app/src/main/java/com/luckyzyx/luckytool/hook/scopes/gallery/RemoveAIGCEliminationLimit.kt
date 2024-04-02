package com.luckyzyx.luckytool.hook.scopes.gallery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
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
                constructor { paramCount(2..3) }.hook {
                    before {
                        args.forEachIndexed { index, it ->
                            if (it is Boolean) args(index).setFalse()
                        }
                    }
                }
            }
        }

        //Source EliminateStack
        dexKitBridge.findClass {
            matcher {
                addFieldForType(IntType)
                addFieldForType(StringClass)
                addFieldForType(BooleanType)
                addMethod { name("equals") }
                addMethod { name("hashCode") }
                addMethod { name("toString") }
                usingStrings("EliminateSaveEntry", "isContentSensitive")
            }
        }.apply {
            checkDataList("EliminateSaveEntry")

            single().name.toClass().apply {
                constructor { paramCount(6..7) }.hook {
                    before {
                        args.forEachIndexed { index, it ->
                            if (it is Boolean) args(index).setFalse()
                        }
                        args().last().setTrue()
                    }
                }
            }
        }
    }
}