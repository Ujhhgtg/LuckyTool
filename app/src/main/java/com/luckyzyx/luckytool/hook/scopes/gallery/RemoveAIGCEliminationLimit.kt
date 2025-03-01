package com.luckyzyx.luckytool.hook.scopes.gallery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveAIGCEliminationLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source EliminateDetectInfo / PanoramicSegmentationInfo
        dexKitBridge.findClass {
            matcher {
                addFieldForType(BooleanType)
                addMethod { name("equals") }
                addMethod { name("hashCode") }
                addMethod { name("toString") }
                usingStrings("Info", "isContentSensitive")
            }
        }.apply {
            checkDataList("EliminateDetectInfo")

            single().name.toClass().apply {
                constructor { param { it.contains(BooleanType) } }.hook {
                    before {
                        args.forEachIndexed { index, it ->
                            if (it is Boolean) args(index).setFalse()
                            if (it?.javaClass?.isEnum == true) args(index).setNull()
                        }
                    }
                }
            }
        }

        //Source EliminateStack / PanoramicSegmentationStack
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
                constructor { param { it.contains(BooleanType) } }.hook {
                    before {
                        args.forEachIndexed { index, it ->
                            if (it is Boolean) args(index).setFalse()
                            if (it?.javaClass?.isEnum == true) args(index).setNull()
                        }
                        if (args.last() is Boolean) args().last().setTrue()
                    }
                }
            }
        }
    }
}