package com.luckyzyx.luckytool.hook.scopes.systemui

import androidx.appcompat.app.AlertDialog
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AutoTapStartRecordingOrCastingDialog : YukiBaseHooker() {
    override fun onHook() {
        //Source MediaProjectionPermissionActivity
        (VariousClass(
            "com.android.systemui.media.MediaProjectionPermissionActivity", //C15
            "com.android.systemui.mediaprojection.permission.MediaProjectionPermissionActivity"
        ).toClass() as Class<Any>).resolve().apply {
            firstMethod { name = "onCreate" }.hook {
                after {
                    firstMethod { name = "onClick";parameterCount = 2 }.of(instance).invoke(
                        null, AlertDialog.BUTTON_POSITIVE
                    )
                }
            }
        }
    }
}