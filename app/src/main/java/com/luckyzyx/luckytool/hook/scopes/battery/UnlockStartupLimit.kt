package com.luckyzyx.luckytool.hook.scopes.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class UnlockStartupLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    override fun onHook() {
        val recordDatabase = "com.oplus.startupapp.data.database.RecordDatabase"

        //Source StartupManager.java
        //Search -> ? 5 : 20; -> Method
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addFieldForType(ContextClass)
                    addFieldForType(recordDatabase)
                    usingStrings("StartupManager")
                    addMethod { paramCount(0);returnType(IntType) }
                    addMethod { paramTypes(IntentClass);returnType(UnitType) }
                    addMethod { paramTypes(BundleClass);returnType(UnitType) }
                }
                paramCount(0)
                returnType(IntType)
                usingNumbers(5, 20)
            }
        }.apply {
            checkDataList("UnlockStartupLimit")
            single().className.toClass().apply {
                method { name = single().methodName;emptyParam();returnType = IntType }.hook {
                    replaceTo(999)
                }
            }
        }
    }
}