package com.luckyzyx.luckytool.service.controller

import android.content.Intent
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IGoogleServiceController
import com.luckyzyx.luckytool.utils.LogUtils
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.ipc.RootService

@Obfuscate
class GoogleServiceControllerService : RootService() {
    val tag = "GoogleServiceControllerService"

    companion object {
        //Source Settings OplusGoogleSettingsFragment
        private const val key = "customize_control_cn_gms"
    }

    override fun onBind(intent: Intent) = object : IGoogleServiceController.Stub() {
        override fun getGoogleStatus(): Boolean {
            return try {
                val result = ShellUtils.fastCmd("settings get system $key")
                LogUtils.d(tag, "getGoogleStatus", "result -> $result")
                result.toIntOrNull() == 1
            } catch (e: Exception) {
                LogUtils.d(tag, "getGoogleStatus", "$e")
                false
            }
        }

        override fun setGoogleStatus(status: Boolean) {
            try {
                val result = ShellUtils.fastCmdResult(
                    "settings put system $key ${if (status) 1 else 0}"
                )
                LogUtils.d(tag, "setGoogleStatus", "$status -> $result")
            } catch (e: Exception) {
                LogUtils.d(tag, "setGoogleStatus", "$e")
            }
        }
    }
}