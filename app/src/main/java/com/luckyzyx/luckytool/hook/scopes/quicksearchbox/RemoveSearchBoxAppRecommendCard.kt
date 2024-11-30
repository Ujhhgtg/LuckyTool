package com.luckyzyx.luckytool.hook.scopes.quicksearchbox

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveSearchBoxAppRecommendCard : YukiBaseHooker() {
    override fun onHook() {
        //Source AliveAppRecommendView -> view_alive_app
        "com.heytap.quicksearchbox.ui.widget.AliveAppRecommendView".toClass().apply {
            method {
                param { it[0] == ListClass && it[1] == BooleanType }
                paramCount(2..4)
            }.hook {
                before {
                    args().first().cast<java.util.ArrayList<Any>>()?.clear()
                }
            }
        }
    }
}