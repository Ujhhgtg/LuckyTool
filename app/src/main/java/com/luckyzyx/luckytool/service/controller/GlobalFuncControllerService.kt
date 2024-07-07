package com.luckyzyx.luckytool.service.controller

import android.content.Intent
import android.os.SystemProperties
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IGlobalFuncController
import com.luckyzyx.luckytool.utils.formatSpace
import com.luckyzyx.luckytool.utils.replaceSpace
import com.topjohnwu.superuser.ipc.RootService
import java.io.File

@Obfuscate
class GlobalFuncControllerService : RootService() {
    override fun onBind(intent: Intent) = object : IGlobalFuncController.Stub() {

        override fun getFileText(dir: String): String {
            val file = File(dir)
            return if (file.exists() && file.isFile) file.readText() else ""
        }

        override fun getOtaVersion(): String {
            return SystemProperties.get("ro.build.version.ota", "null")
        }

        override fun getMarketName(): String {
            return SystemProperties.get("ro.vendor.oplus.market.name", "null")

        }

        override fun getLcdInfo(): String {
            val text = getFileText("/proc/devinfo/lcd")
            return if (text.isBlank()) "null" else {
                val list = text.lines().toMutableList()
                list.forEachIndexed { index, s ->
                    val value = s.substringAfterLast(":").replace("\t", "").uppercase()
                    list[index] = value
                }
                "${list[0]} ${list[1]}"
            }
        }

        override fun getFlashInfo(): String {
            val text = getFileText("/sys/class/block/sda/device/inquiry")
            return if (text.isBlank()) "null" else formatSpace(text)
        }

        override fun getPcbInfo(): String {
            val gms = SystemProperties.get("gsm.serial", "")
            val vendor = SystemProperties.get("vendor.gsm.serial", "null")
            return (gms + vendor).replaceSpace

        }

        override fun getSnInfo(): String {
            return SystemProperties.get("ro.serialno", "null")
        }

        override fun getPrjNameInfo(): String {
            return SystemProperties.get("ro.boot.prjname", "null")
        }

        override fun getSlotInfo(): String {
            return SystemProperties.get("ro.boot.slot_suffix", "null")
        }
    }
}