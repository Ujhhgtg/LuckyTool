package com.luckyzyx.luckytool.hook.hookers

import android.os.SystemProperties
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.topjohnwu.superuser.ShellUtils

object HookAudioMonitor : YukiBaseHooker() {
    private const val prop = "ro.oplus.audio.voip_record_white_app_support"
    override fun onHook() {
        //Source OplusVoipRecorderService
        "com.oplus.audiomonitor.voiprecord.OplusVoipRecorderService".toClass().apply {
            method { name = "onCreate" }.hook {
                after {
                    val isSupport = SystemProperties.getBoolean(prop, false)
                    if (!isSupport) ShellUtils.fastCmd("setprop $prop true")
                }
            }
        }
    }
}