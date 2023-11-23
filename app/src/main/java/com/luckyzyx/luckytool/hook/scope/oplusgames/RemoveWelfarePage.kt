package com.luckyzyx.luckytool.hook.scope.oplusgames

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.UnitType

object RemoveWelfarePage : YukiBaseHooker() {
    override fun onHook() {
        val mainPanelView = "business.mainpanel.MainPanelView".toClassOrNull()
        if (mainPanelView == null) {
            "business.mainpanel.main.MainPanelFragment".toClass().apply {
                method { name = "addRadioButton" }.hook {
                    before {
                        if (args().first().string() == "welfare") resultNull()
                    }
                }
            }
            return
        }

        //Source MainPanelView
        mainPanelView.apply {
            method {
                param { it[0] == ListClass && it[1] == BooleanType }
                paramCount(2..3)
                returnType = UnitType
            }.hook {
                before {
                    val list = args().first().list<Any>()
                    val first = list.getOrNull(0) ?: return@before
                    args().first().set(ArrayList(arrayListOf(first)))
                }
            }
        }
    }
}