package com.luckyzyx.luckytool.hook.scopes.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveAdsFromDownloadDialog(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source DownloadCardAdProvider
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addFieldForType(ContextClass)
                    addFieldForType(StringClass)
                    addMethod {
                        paramTypes(ContextClass, IntType)
                        returnType(UnitType)
                    }
                    usingStrings("DownloadCardAdProvider")
                }
                usingStrings("DownloadCardAdProvider", "createAdRequest", "appName", "posIds")
            }
        }.apply {
            checkDataList("RemoveAdsFromDownloadDialog")
            single().className.toClass().apply {
                method {
                    name = single().methodName
                }.hook {
                    intercept()
                }
            }
        }
    }
}