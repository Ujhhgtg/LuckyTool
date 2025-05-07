package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveGreenDotPrivacyPrompt : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 34) loadHooker(GreenDotPrivacyPrompt)
        else if (osCode >= 30) loadHooker(GreenDotPrivacyPromptV14)
        else loadHooker(GreenDotPrivacyPromptV13)
    }

    @Obfuscate
    object GreenDotPrivacyPrompt : YukiBaseHooker() {
        override fun onHook() {
            //Source ScreenDecorations
            "com.android.systemui.ScreenDecorations".toClass().apply {
                method {
                    name = "updateOverlayWindowVisibilityIfViewExists"
                    param(ViewClass)
                }.hook {
                    intercept()
                }
            }
        }
    }

    @Obfuscate
    object GreenDotPrivacyPromptV14 : YukiBaseHooker() {
        override fun onHook() {
            //Source ScreenDecorations
            "com.android.systemui.ScreenDecorations".toClass().apply {
                method {
                    name = "updateOverlayWindowVisibilityIfViewExists"
                    param(ViewClass)
                }.hook {
                    intercept()
                }
            }
            //二次处理
            //Source OplusPrivacyDotViewController
            "com.oplus.systemui.privacy.OplusPrivacyDotViewController".toClass().apply {
                method { name = "showDotView";paramCount = 2 }.hook {
                    intercept()
                }
                method { name = "updateDesignatedCorner";paramCount = 2 }.hook {
                    intercept()
                }
            }
        }
    }

    @Obfuscate
    object GreenDotPrivacyPromptV13 : YukiBaseHooker() {
        override fun onHook() {
            //Source PrivacyDotViewController
            "com.android.systemui.statusbar.events.PrivacyDotViewController".toClass().apply {
                method { name = "showDotView";paramCount = 2 }.hook {
                    intercept()
                }
                method { name = "updateDesignatedCorner";paramCount = 2 }.hook {
                    intercept()
                }
            }
            //二次处理
            //Source OplusPrivacyDotViewController
            "com.oplusos.systemui.statusbar.events.OplusPrivacyDotViewController".toClass().apply {
                method { name = "showDotView";paramCount = 2 }.hook {
                    intercept()
                }
                method { name = "updateDesignatedCorner";paramCount = 2 }.hook {
                    intercept()
                }
            }
        }
    }
}