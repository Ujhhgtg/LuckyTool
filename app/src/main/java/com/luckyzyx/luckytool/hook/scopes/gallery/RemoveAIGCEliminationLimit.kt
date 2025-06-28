package com.luckyzyx.luckytool.hook.scopes.gallery

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveAIGCEliminationLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source EliminateDetectInfo / PanoramicSegmentationInfo
        dexKitBridge.findClass {
            matcher {
                addFieldForType(Boolean::class.java)
                addMethod { name("equals") }
                addMethod { name("hashCode") }
                addMethod { name("toString") }
                usingStrings("Info", "isContentSensitive")
            }
        }.apply {
            checkDataList("EliminateDetectInfo")

            single().name.toClass().resolve().apply {
                firstConstructor { parameters { it.contains(Boolean::class.java) } }.hook {
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
                addFieldForType(Int::class.java)
                addFieldForType(String::class.java)
                addFieldForType(Boolean::class.java)
                addMethod { name("equals") }
                addMethod { name("hashCode") }
                addMethod { name("toString") }
                usingStrings("EliminateSaveEntry", "isContentSensitive")
            }
        }.apply {
            checkDataList("EliminateSaveEntry")

            single().name.toClass().resolve().apply {
                firstConstructor { parameters { it.contains(Boolean::class.java) } }.hook {
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