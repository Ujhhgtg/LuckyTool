package com.luckyzyx.luckytool.hook.scope.oplusgames

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import java.util.concurrent.CopyOnWriteArrayList

object RemoveWelfarePage : YukiBaseHooker() {
    override fun onHook() {
        //Source MainPanelView
        "business.mainpanel.MainPanelView".toClass().apply {
            method {
                param { it[0] == ListClass && it[1] == BooleanType }
                paramCount(2..3)
                returnType = UnitType
            }.hook {
                before {
                    args().first().cast<CopyOnWriteArrayList<Any>>()?.apply {
                        removeIf { indexOf(it) != 0 }
                    }
                }
            }
        }
    }
}