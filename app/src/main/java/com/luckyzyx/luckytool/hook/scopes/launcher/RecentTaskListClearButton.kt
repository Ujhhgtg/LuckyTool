package com.luckyzyx.luckytool.hook.scopes.launcher

import android.widget.Button
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RecentTaskListClearButton : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusClearAllPanelView
        "com.oplus.quickstep.views.OplusClearAllPanelView".toClass().apply {
            method { name = "inflateIfNeeded" }.hook {
                after {
                    field { name = "mClearAllBtn" }.get(instance).cast<Button>()?.isVisible = false
                }
            }
        }
    }
}