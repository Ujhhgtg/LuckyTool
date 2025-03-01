package com.luckyzyx.luckytool.hook.scopes.directui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveTouchAppRecommendCard : YukiBaseHooker() {
    override fun onHook() {
        //Source DirectUIMainViewMode -> AppBean
        "com.coloros.directui.repository.datasource.AppBean".toClass().apply {
            method { name = "toCardUIInfo" }.hook {
                intercept()
            }
        }
    }
}