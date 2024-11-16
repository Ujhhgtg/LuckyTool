package com.luckyzyx.luckytool.hook.scopes.audiomonitor

import android.app.Service
import android.os.SystemProperties
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.showToast
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils

@Obfuscate
object HookVoipRecorderService : YukiBaseHooker() {
    private const val PROP = "ro.oplus.audio.voip_record_white_app_support"
    override fun onHook() {
        //Source OplusVoipRecorderService
        "com.oplus.audiomonitor.voiprecord.OplusVoipRecorderService".toClass().apply {
            method { name = "onStartCommand" }.hook {
                before {
                    val service = instance<Service>()
                    val isSupport = SystemProperties.getBoolean(PROP, false)
                    if (!isSupport) {
                        if (Shell.getShell().isRoot) ShellUtils.fastCmd("setprop $PROP true")
                        else service.showToast("No Root!")
                    }
                }
            }
        }
    }
}