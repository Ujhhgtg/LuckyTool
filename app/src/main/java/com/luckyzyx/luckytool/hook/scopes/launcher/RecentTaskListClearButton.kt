package com.luckyzyx.luckytool.hook.scopes.launcher

import android.widget.Button
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RecentTaskListClearButton : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusClearAllPanelView
        "com.oplus.quickstep.views.OplusClearAllPanelView".toClass().apply {
            val hasInflate = hasMethod { name = "inflateIfNeeded" }
            if (hasInflate) {
                method { name = "inflateIfNeeded" }.hook {
                    after {
                        field { name = "mClearAllBtn" }.get(instance).cast<Button>()?.isVisible = false
                    }
                }
            } else {
                method { name = "onFinishInflate" }.hook {
                    after {
                        field { name = "mClearAllBtn" }.get(instance).cast<Button>()?.isVisible = false
                    }
                }
            }
        }
    }
}