package com.luckyzyx.luckytool.hook.scopes.beaconlink

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.HashMapClass
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveBeaconLinkTimeLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source IDs
        dexKitBridge.findClass {
            matcher {
                addFieldForType(StringClass)
                addFieldForType(LongType)
                fieldCount(2)
                addMethod {
                    paramTypes(StringClass)
                    returnType(BooleanType)
                    addCaller {
                        paramTypes(StringClass)
                        returnType(BooleanType)
                    }
                    addCaller {
                        paramTypes(ContextClass, ListClass)
                        returnType(HashMapClass)
                    }
                }
            }
        }.apply {
            checkDataList("RemoveBeaconLinkTimeLimit Clazz")
            single().name.toClass().apply {
                method {
                    param(StringClass)
                    returnType = BooleanType
                }.hook {
                    replaceToTrue()
                }
            }
        }
    }
}