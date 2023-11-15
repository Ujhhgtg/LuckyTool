package com.luckyzyx.luckytool.hook.scope.oplusgames

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.UnitType

object RemoveToolRecommendationCard : YukiBaseHooker() {
    override fun onHook() {
        //Source ToolsRecommendCardLayout
        "business.module.toolsrecommend.ToolsRecommendCardLayout".toClass().apply {
            method { param(ListClass);returnType = UnitType }.hook {
                before {
                    args().first().set(ArrayList<Any>())
                }
            }
        }
    }
}