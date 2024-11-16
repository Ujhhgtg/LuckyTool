package com.luckyzyx.luckytool.hook.scopes.games

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveGameAssistantTemperatureDetection : YukiBaseHooker() {
    override fun onHook() {
        //Source CoolingBubbleTipsHelper
        "business.module.perfmode.CoolingBubbleTipsHelper".toClass().field { type = IntType }
            .all().forEach {
                if (it.int() > 0) it.set(100)
            }
    }
}