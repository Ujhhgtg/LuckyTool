package com.luckyzyx.luckytool.hook.scopes.oplusgames

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.AnyClass

object EnableXModeFeature : YukiBaseHooker() {
    override fun onHook() {
        //Source CoolingBackClipHelper / CoolingBackClipFeature
        VariousClass(
            "business.module.perfmode.CoolingBackClipHelper", //V8
            "business.module.perfmode.CoolingBackClipFeature" //V9.0.0
        ).toClass().apply {
            method { paramCount = 1;returnType = AnyClass }.hook {
                after { resultTrue() }
            }
        }
    }
}