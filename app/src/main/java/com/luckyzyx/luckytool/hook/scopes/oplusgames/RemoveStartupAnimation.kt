package com.luckyzyx.luckytool.hook.scopes.oplusgames

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object RemoveStartupAnimation : YukiBaseHooker() {
    override fun onHook() {
        //Source GameOptimizedNewView
        //Search -> startAnimationIn -> Method
        "business.secondarypanel.view.GameOptimizedNewView".toClass().apply {
            method { name = "c" }.hook {
                intercept()
            }
        }
    }
}
