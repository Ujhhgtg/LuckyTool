package com.luckyzyx.luckytool.service.controller

import android.content.Intent
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.ITouchPanelController
import com.luckyzyx.luckytool.utils.LogUtils
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.ipc.RootService
import okhttp3.internal.toHexString
import java.io.File

@Obfuscate
class TouchPanelControllerService : RootService() {
    val tag = "TouchPanelControllerService"

    companion object {
        private const val touchPanelDir = "/proc/touchpanel/game_switch_enable"
        private const val touchHidlDir = "/odm/bin/touchHidlTest"
        private val touchPanel = File(touchPanelDir)
        private val touchHidl = File(touchHidlDir)
        private val mode = if (touchPanel.exists()) 1 else if (touchHidl.exists()) 2 else 0

        private const val readTouch = "touchHidlTest -c ro 0 26"
        private const val writeTouch = "touchHidlTest -c wo 0 26"
    }

    override fun onBind(intent: Intent) = object : ITouchPanelController.Stub() {
        override fun checkTouchMode(): Boolean {
            return try {
                mode != 0
            } catch (e: Throwable) {
                LogUtils.e(tag, "checkTouchMode", "$e", true)
                false
            }
        }

        override fun getTouchMode(): Int {
            return try {
                when (mode) {
                    1 -> touchPanel.readText().substringBefore(",").toInt()
                    2 -> ShellUtils.fastCmd(readTouch).substringBefore(",").toInt()
                    else -> 0
                }
            } catch (e: Throwable) {
                LogUtils.e(tag, "getTouchMode", "$e", true)
                0
            }
        }

        override fun setTouchMode(value: Int) {
            try {
                val int16 = value.toHexString()
                when (mode) {
                    1 -> touchPanel.writeText(int16)
                    2 -> ShellUtils.fastCmd("$writeTouch $int16")
                }
            } catch (e: Throwable) {
                LogUtils.e(tag, "setTouchMode", "$e", true)
            }
        }
    }
}