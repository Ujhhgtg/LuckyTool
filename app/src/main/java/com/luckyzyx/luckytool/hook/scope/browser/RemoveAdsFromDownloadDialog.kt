package com.luckyzyx.luckytool.hook.scope.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList

object RemoveAdsFromDownloadDialog : YukiBaseHooker() {
    override fun onHook() {
        val adRequest = "com.opos.feed.api.params.AdRequest"
        val feedAdNative = "com.opos.feed.api.FeedAdNative"
        val recyclerAdHelper = "com.opos.feed.api.RecyclerAdHelper"
        val adInteractionListener = "com.opos.feed.api.params.AdInteractionListener"

        //Source DownloadCardAdProvider
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
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
                            paramTypes(ContextClass.name, IntType.name)
                            returnType(UnitType)
                        }
                        add { returnType(adRequest) }
                        add { returnType(recyclerAdHelper) }
                    }
                    usingStrings("DownloadCardAdProvider")
                }
            }.apply {
                checkDataList("RemoveAdsFromDownloadDialog")
                first().name.toClass().apply {
                    method {
                        paramCount(1)
                        returnType(adRequest)
                    }.hook { replaceTo(null) }
                }
            }
        }
    }
}