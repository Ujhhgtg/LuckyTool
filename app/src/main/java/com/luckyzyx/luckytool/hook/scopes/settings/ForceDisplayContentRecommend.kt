package com.luckyzyx.luckytool.hook.scopes.settings

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object ForceDisplayContentRecommend : YukiBaseHooker() {
    override fun onHook() {
        //Source RecommendController
        VariousClass(
            "com.oplus.settings.feature.othersettings.controller.RecommendController", //C13 C14
            "com.oplus.settings.feature.spfunction.RecommendController" //C14.1
        ).toClass().apply {
            method { name = "getAvailabilityStatus" }.hook {
                replaceTo(0)
            }
        }
    }
}