package com.luckyzyx.luckytool.hook.scopes.browser

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveAdsFromDownloadDialog(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source DownloadCardAdProvider
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addFieldForType(Context::class.java)
                    addFieldForType(String::class.java)
                    addMethod {
                        paramTypes(Context::class.java, Int::class.java)
                        returnType(Void.TYPE)
                    }
                    usingStrings("DownloadCardAdProvider")
                }
                usingStrings("DownloadCardAdProvider", "createAdRequest", "appName", "posIds")
            }
        }.apply {
            checkDataList("RemoveAdsFromDownloadDialog")
            single().className.toClass().resolve().apply {
                firstMethod {
                    name = single().methodName
                }.hook {
                    intercept()
                }
            }
        }
    }
}