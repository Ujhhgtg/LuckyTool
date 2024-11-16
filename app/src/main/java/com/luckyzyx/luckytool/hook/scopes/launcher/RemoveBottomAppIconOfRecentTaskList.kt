package com.luckyzyx.luckytool.hook.scopes.launcher

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object RemoveBottomAppIconOfRecentTaskList : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        //Source DockView
        "com.oplus.quickstep.dock.DockView".toClass().apply {
            method {
                name = if (osCode >= 33) "updateCurveProperties"
                else "setVisibilityAlpha"
            }.hookAll {
                after {
                    instance<View>().isVisible = false
                }
            }
        }
    }
}