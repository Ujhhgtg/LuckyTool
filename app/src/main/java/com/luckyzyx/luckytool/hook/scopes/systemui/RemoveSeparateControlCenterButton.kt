package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object RemoveSeparateControlCenterButton : YukiBaseHooker() {
    override fun onHook() {
        val hideEdit = prefs(ModulePrefs).getBoolean("remove_control_center_edit_button", false)
        val hideMore = prefs(ModulePrefs).getBoolean("remove_control_center_more_button", false)

        //Source OplusQSBottomViewController
        "com.oplus.systemui.plugins.qs.bottom.OplusQSBottomViewController".toClass().apply {
            method { name = "init" }.hook {
                after {
                    if (hideEdit) field { name = "editBtn" }.get(instance).cast<View>()
                        ?.isVisible = false
                    if (hideMore) field { name = "moreBtn" }.get(instance).cast<View>()
                        ?.isVisible = false
                }
            }
        }
    }
}