package com.luckyzyx.luckytool.hook.scopes.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveAdsFromDownloadDialog(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val adRequest = "com.opos.feed.api.params.AdRequest"
        val feedAdNative = "com.opos.feed.api.FeedAdNative"
        val recyclerAdHelper = "com.opos.feed.api.RecyclerAdHelper"
        val adInteractionListener = "com.opos.feed.api.params.AdInteractionListener"

        //Source DownloadCardAdProvider
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ContextClass.name)
                    addForType(StringClass.name)
                    addForType(feedAdNative)
                    addForType(recyclerAdHelper)
                    addForType(adInteractionListener)
                }
                methods {
                    add {
                        paramTypes(ContextClass, IntType)
                        returnType(UnitType)
                    }
                    add { returnType(adRequest) }
                    add { returnType(recyclerAdHelper) }
                }
                usingStrings("DownloadCardAdProvider")
            }
        }.apply {
            checkDataList("RemoveAdsFromDownloadDialog")
            single().name.toClass().apply {
                method {
                    paramCount(1)
                    returnType(adRequest)
                }.hook { replaceTo(null) }
            }
        }
    }
}