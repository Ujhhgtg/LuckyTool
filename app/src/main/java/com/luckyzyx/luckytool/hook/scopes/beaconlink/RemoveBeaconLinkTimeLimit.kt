package com.luckyzyx.luckytool.hook.scopes.beaconlink

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveBeaconLinkTimeLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source IDs
        dexKitBridge.findClass {
            matcher {
                addFieldForType(String::class.java)
                addFieldForType(Long::class.java)
                fieldCount(2)
                addMethod {
                    paramTypes(String::class.java)
                    returnType(Boolean::class.java)
                    addCaller {
                        paramTypes(String::class.java)
                        returnType(Boolean::class.java)
                    }
                    addCaller {
                        paramTypes(Context::class.java, List::class.java)
                        returnType(HashMap::class.java)
                    }
                }
            }
        }.apply {
            checkDataList("RemoveBeaconLinkTimeLimit Clazz")
            single().name.toClass().resolve().apply {
                firstMethod {
                    parameters(String::class)
                    returnType = Boolean::class
                }.hook {
                    replaceToTrue()
                }
            }
        }
    }
}