package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object AutoTapStartRecordingOrCastingDIalog : YukiBaseHooker() {
    override fun onHook() {
        //Source MediaProjectionPermissionActivity
        "com.android.systemui.media.MediaProjectionPermissionActivity".toClass().apply {
            method { name = "onCreate" }.hook {
                after {
                    method { name = "grantMediaProjectionPermission" }.get(instance).call(0)
                }
            }
        }
    }
}