package com.luckyzyx.luckytool.hook.scopes.battery

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class UnlockStartupLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    override fun onHook() {
        //Source StartupManager.java
        //Search -> ? 5 : 20; -> Method
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(AnyClass.name)
                    addForType(ContextClass.name)
                    count(4..6)
                }
                methods {
                    add { paramCount(0);returnType(IntType) }
                    add { paramTypes(IntentClass);returnType(UnitType) }
                    add { paramTypes(BundleClass);returnType(UnitType) }
                }
            }
        }.apply {
            checkDataList("UnlockStartupLimit")
            single().name.toClass().apply {
                method { emptyParam();returnType = IntType }.hook {
                    replaceTo(999)
                }
            }
        }
    }
}