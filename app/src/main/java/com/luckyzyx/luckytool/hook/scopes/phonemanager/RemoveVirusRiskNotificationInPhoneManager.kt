package com.luckyzyx.luckytool.hook.scopes.phonemanager

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveVirusRiskNotificationInPhoneManager(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source VirusScanNotifyListener
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(Context::class.java)
                    addForType(String::class.java)
                }
                methods {
                    add { paramTypes(ArrayList::class.java) }
                    add { returnType(Int::class.java) }
                    add { returnType(String::class.java) }
                }
                usingStrings("VirusScanNotifyListener")
            }
        }.apply {
            checkDataList("RemoveVirusRiskNotificationInPhoneManager")
            single().name.toClass().resolve().apply {
                method { parameters(ArrayList::class) }.hookAll {
                    intercept()
                }
            }
        }
    }
}