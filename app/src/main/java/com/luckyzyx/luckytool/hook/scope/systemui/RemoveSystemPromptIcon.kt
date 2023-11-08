package com.luckyzyx.luckytool.hook.scope.systemui

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method

object RemoveSystemPromptIcon : YukiBaseHooker() {
    override fun onHook() {
        //Source SystemPromptView
        VariousClass(
            "com.oplusos.systemui.statusbar.widget.SystemPromptView", //C13
            "com.oplus.systemui.statusbar.widget.SystemPromptView" //C14
        ).toClass().apply {
            method { name = "updateViewVisible" }.hook {
                before {
                    instance<View>().isVisible = false
                    resultNull()
                }
            }
            if (hasMethod { name = "setViewVisibleByDisable" }) method {
                name = "setViewVisibleByDisable"
            }.hook {
                before {
                    instance<View>().isVisible = false
                    resultNull()
                }
            }
        }
    }
}