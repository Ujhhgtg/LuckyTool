package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveSeparateControlCenterButton : YukiBaseHooker() {
    override fun onHook() {
        val hideEdit = prefs(ModulePrefs).getBoolean("remove_control_center_edit_button", false)
        val hideMore = prefs(ModulePrefs).getBoolean("remove_control_center_more_button", false)

        //Source OplusQSBottomViewController
        "com.oplus.systemui.plugins.qs.bottom.OplusQSBottomViewController".toClass().resolve()
            .apply {
                firstMethod { name = "init" }.hook {
                    after {
                        if (hideEdit) firstField { name = "editBtn" }.of(instance).get<View>()
                            ?.isVisible = false
                        if (hideMore) firstField { name = "moreBtn" }.of(instance).get<View>()
                            ?.isVisible = false
                    }
                }
            }
    }
}