package com.luckyzyx.luckytool.hook.scope.oplusgames

import android.os.Bundle
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveRootCheck(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source COSASDKManager
        //Search getSupportCoolEx new Bundle -> Class
        //Search getFeature -> dynamic_feature_cool_ex
        //isSafe:null; -> isSafe:0
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(StringClass.name)
                    addForType(BooleanType.name)
                    addForType(IntType.name)
                }
                methods {
                    add { name = "clear";paramCount(0) }
                    add { paramCount(0);returnType(BundleClass) }
                }
            }
        }.apply {
            checkDataList("RemoveRootCheck")
            single().name.toClass().apply {
                method { emptyParam();returnType = BundleClass }.hook {
                    after { result<Bundle>()?.putInt("isSafe", 0) }
                }
            }
        }
    }
}
