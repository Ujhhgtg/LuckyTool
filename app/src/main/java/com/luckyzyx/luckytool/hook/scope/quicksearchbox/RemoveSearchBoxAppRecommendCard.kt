package com.luckyzyx.luckytool.hook.scope.quicksearchbox

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ListClass

object RemoveSearchBoxAppRecommendCard : YukiBaseHooker() {
    override fun onHook() {
        //Source AliveAppRecommendView -> view_alive_app
        "com.heytap.quicksearchbox.ui.widget.AliveAppRecommendView".toClass().apply {
            method {
                param { it[0] == ListClass && it[1] == BooleanType && it[2] == BooleanType }
                paramCount(3..4)
            }.hook {
                before {
                    args().first().cast<java.util.ArrayList<Any>>()?.clear()
                }
            }
        }
    }
}