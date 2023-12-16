package com.luckyzyx.luckytool.hook.scope.phonemanager

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.ArrayListClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveVirusRiskNotificationInPhoneManager(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source VirusScanNotifyListener
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ContextClass.name)
                    addForType(StringClass.name)
                }
                methods {
                    add { paramTypes(ArrayListClass) }
                    add { returnType(IntType) }
                    add { returnType(StringClass) }
                }
                usingStrings("VirusScanNotifyListener")
            }
        }.apply {
            checkDataList("RemoveVirusRiskNotificationInPhoneManager")
            single().name.toClass().apply {
                method { param(ArrayListClass) }.hookAll {
                    intercept()
                }
            }
        }
    }
}