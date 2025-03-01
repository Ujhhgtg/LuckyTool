package com.luckyzyx.luckytool.hook.scopes.systemui

import androidx.appcompat.app.AlertDialog
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AutoTapStartRecordingOrCastingDialog : YukiBaseHooker() {
    override fun onHook() {
        //Source MediaProjectionPermissionActivity
        VariousClass(
            "com.android.systemui.media.MediaProjectionPermissionActivity", //C15
            "com.android.systemui.mediaprojection.permission.MediaProjectionPermissionActivity"
        ).toClass().apply {
            method { name = "onCreate" }.hook {
                after {
                    method { name = "onClick";paramCount = 2 }.get(instance).call(
                        null, AlertDialog.BUTTON_POSITIVE
                    )
                }
            }
        }
    }
}