package com.luckyzyx.luckytool.hook.scopes.launcher

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveBottomAppIconOfRecentTaskList : YukiBaseHooker() {
    override fun onHook() {
        //Source DockView
        "com.oplus.quickstep.dock.DockView".toClass().apply {
            val hasSetVisibilityAlpha = hasMethod { name = "setVisibilityAlpha" }
            if (hasSetVisibilityAlpha) method { name = "setVisibilityAlpha" }.hook {
                after {
                    instance<View>().isVisible = false
                }
            }
            else constructor().hookAll {
                after {
                    instance<View>().isVisible = false
                }
            }
        }

        //Source DockViewController C14+
        "com.oplus.quickstep.dock.DockViewController".toClassOrNull()?.apply {
            method { name = "onRecentsViewOrientationChange" }.hook {
                before {
                    args().first().setFalse()
                }
            }
            method { name = "updateOnTaskDisplayModeChange" }.hook {
                before {
                    args().first().setTrue()
                }
            }
            method { name = "updateOnLauncherMultiWindowChange" }.hook {
                before {
                    args().first().setTrue()
                }
            }
        }
    }
}