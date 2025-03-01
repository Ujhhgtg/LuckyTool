package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object RemoveSystemPromptIcon : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        loadHooker(SystemPromptIconV13)
    }

    @Obfuscate
    object SystemPromptIconV13 : YukiBaseHooker() {
        override fun onHook() {
            //Source SystemPromptController
            VariousClass(
                "com.oplusos.systemui.statusbar.policy.SystemPromptController", //C13
                "com.oplus.systemui.statusbar.controller.SystemPromptController" //C14
            ).toClass().apply {
                method { name = "updatePromptIcon" }.hook {
                    intercept()
                }
            }
        }
    }
}