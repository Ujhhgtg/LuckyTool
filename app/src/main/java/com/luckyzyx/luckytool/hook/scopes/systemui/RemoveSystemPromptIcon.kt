package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.getOSVersionCode

object RemoveSystemPromptIcon : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 30) loadHooker(SeedlingCardIcon)
        else loadHooker(SystemPromptIconV13)
    }

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

    object SeedlingCardIcon : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusSeedlingCardController
            "com.oplus.systemui.statusbar.seeding.OplusSeedlingCardController".toClass().apply {
                method { name = "updateCapsuleContainer" }.hook {
                    before {
                        args().first().setFalse()
                    }
                }
            }
        }
    }
}