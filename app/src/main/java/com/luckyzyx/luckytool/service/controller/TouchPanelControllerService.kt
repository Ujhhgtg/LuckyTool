package com.luckyzyx.luckytool.service.controller

import android.content.Intent
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.ITouchPanelController
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.replaceSpace
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.ipc.RootService
import okhttp3.internal.toHexString
import java.io.File

@Obfuscate
class TouchPanelControllerService : RootService() {
    val tag = "TouchPanelControllerService"

    companion object {
        private const val fileDir = "/proc/touchpanel/game_switch_enable"
        val file = File(fileDir)

        private const val readTouch = "touchHidlTest -c ro 0 26"
        private const val writeTouch = "touchHidlTest -c wo 0 26"
    }

    override fun onBind(intent: Intent) = object : ITouchPanelController.Stub() {
        override fun checkTouchMode(): Boolean {
            return try {
                file.exists() || ShellUtils.fastCmdResult(readTouch)
            } catch (e: Throwable) {
                LogUtils.e(tag, "checkTouchMode", "$e", true)
                false
            }
        }

        override fun getTouchMode(): Int {
            return try {
                if (file.exists()) file.readText().replaceSpace.substringBefore(",").toInt()
                else ShellUtils.fastCmd(readTouch).replaceSpace.substringBefore(",").toInt()
            } catch (e: Throwable) {
                LogUtils.e(tag, "getTouchMode", "$e", true)
                0
            }
        }

        override fun setTouchMode(value: Int) {
            try {
                val int16 = value.toHexString()
                if (file.exists()) file.writeText(int16)
                else callTouchHidl(int16)
            } catch (e: Throwable) {
                LogUtils.e(tag, "setTouchMode", "$e", true)
            }
        }
    }

    fun callTouchHidl(int16: String) {
        val command = arrayOf(
//            "start touchDaemon && ps -A | grep touchDaemon",
            "$writeTouch $int16"
        )
        ShellUtils.fastCmd(*command)
    }
}