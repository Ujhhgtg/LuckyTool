package com.luckyzyx.luckytool.utils

import android.content.Context
import com.android.internal.os.PowerProfile
import com.highcapable.yukihookapi.hook.log.YLog
import com.joom.paranoid.Obfuscate
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.math.roundToInt

@Obfuscate
object DeviceUtils {
    /**
     * 获取GUID
     * /data/system/openid_config.xml
     */
    fun getGuid(): String {
        val command =
            "cat /data/system/openid_config.xml | egrep guid | egrep -o 'value=\"[^\"]+\"' | sed 's/\\\"//g,s/value=//g'"
        return ShellUtils.fastCmd(command).ifBlank { "null" }
    }

    /**
     * 获取招募ID
     * /data/user/0/com.oplus.ota/shared_prefs/persistent_info.xml
     */
    fun getRecruit(): String {
        val command =
            "cat /data/user/0/com.oplus.ota/shared_prefs/persistent_info.xml | egrep ota_register_trigger_id | egrep -o '>[^<]+<' | tr -d '><'"
        return ShellUtils.fastCmd(command).ifBlank { "null" }
    }

    /**
     * 获取主板ID
     * @return String
     */
    fun getDeviceID(): String {
        val serialCommand = "cat /sys/devices/soc0/serial_number"
        val serialNumber = ShellUtils.fastCmd(serialCommand)
        if (serialNumber.isNotBlank()) return serialNumber
        val serialNoCommand = "cat /sys/firmware/devicetree/base/firmware/android/serialno"
        val serialNo = ShellUtils.fastCmd(serialNoCommand)
        if (serialNo.isNotBlank()) return serialNo
        return "null"
    }

    /**
     * 获取闪存信息
     * @return String
     */
    fun getFlashInfo(): String {
        val command = "cat /sys/class/block/sda/device/inquiry"
        return ShellUtils.fastCmd(command).let {
            if ((it.isNotBlank())) formatSpace(it.replaceSpace.uppercase()) else "null"
        }
    }

    /**
     * 获取LCD信息
     */
    fun getLcdInfo(): String {
        val command = "cat /proc/devinfo/lcd | sed 's/^.*\t//g; s/$/\n/g; s/\n/ /g;'"
        return ShellUtils.fastCmd(command).let {
            if ((it.isNotBlank())) it.replaceSpace.uppercase() else "null"
        }
    }

    /**
     * 获取PCB信息
     */
    fun getPcbInfo(): String {
        val command = "echo \$(getprop gsm.serial)\$(getprop vendor.gsm.serial)"
        return ShellUtils.fastCmd(command).ifBlank { "null" }
    }

    /**
     * 获取SN信息
     */
    fun getSnInfo(): String {
        val command = "getprop ro.serialno"
        return ShellUtils.fastCmd(command).ifBlank { "null" }
    }

    /**
     * 获取PrjName信息
     */
    fun getPrjNameInfo(): String {
        val command = "getprop ro.boot.prjname"
        return ShellUtils.fastCmd(command).ifBlank { "null" }
    }

    /**
     * 获取Slot信息
     */
    fun getSlotInfo(): String {
        val command = "getprop ro.boot.slot_suffix"
        return ShellUtils.fastCmd(command).ifBlank { "null" }
    }

    /**
     * 计算本地电池健康度
     * @receiver Context
     * @param isDebug Boolean
     * @return Int
     */
    fun Context.calcLocalHealth(isDebug: Boolean = false): Int {
        val oplusChgSoh = "/sys/class/oplus_chg/battery/battery_soh"
        val oplusChgFcc = "/sys/class/oplus_chg/battery/battery_fcc"
        val powerSupplySoh = "/sys/class/power_supply/battery/batt_soh"
        val powerSupplyFcc = "/sys/class/power_supply/battery/batt_fcc"
        try {
            val sohFile = if (SDK >= A13) File(oplusChgSoh)
            else File(powerSupplySoh)
            LogUtils.d("calcLocalHealth", "sohFile", sohFile.path, isDebug)
            val sohValue = safeOfNull {
                BufferedReader(FileReader(sohFile)).readLine().replaceSpace.toIntOrNull()
            } ?: -1
            LogUtils.d("calcLocalHealth", "sohValue", "$sohValue", isDebug)
            if (sohValue in 1..100) return sohValue

            val fccFile = if (SDK >= A13) File(oplusChgFcc)
            else File(powerSupplyFcc)
            LogUtils.d("calcLocalHealth", "curFile", fccFile.path, isDebug)
            val fccValue = safeOfNull {
                BufferedReader(FileReader(fccFile)).readLine().replaceSpace.toIntOrNull()
            } ?: -1
            LogUtils.d("calcLocalHealth", "curValue", "$fccValue", isDebug)
            if (fccValue <= 0) return -1

            val designValue = PowerProfile(this).batteryCapacity
            LogUtils.d("calcLocalHealth", "designValue", "$designValue", isDebug)
            val calc = safeOfNull { (fccValue / designValue * 100.0).roundToInt() } ?: -1
            LogUtils.d("calcLocalHealth", "calc", "$calc", isDebug)
            return if (calc > 100) calc / 1000 else calc
        } catch (e: Exception) {
            YLog.error("Calc Local Health Error", e)
            return -1
        }
    }

    suspend fun getQSlist(): ArrayList<String> {
        return com.drake.net.utils.withDefault {
            val configList = ArrayList<String>()
            val indexList = ArrayList<String>()
            val command1 =
                "ls -f /data/data/com.tencent.mobileqq/databases/ | egrep 'config_db([0-9]+)\$' | | sed 's/config_db//g'"
            val command2 =
                "ls -f /data/data/com.tencent.mobileqq/databases/ | egrep '([0-9]+)-IndexQQMsg.db' | sed 's/-IndexQQMsg.db//g'"
            Shell.cmd(command1).to(configList).exec()
            Shell.cmd(command2).to(indexList).exec()
//        LogUtils.e("getQSlist", "cachelist", "${list.toList()}", true)
            ArrayList(configList.union(indexList))
        }
    }

    suspend fun getCSid(): ArrayList<String> {
        val command =
            "cat /data/data/com.coolapk.market/shared_prefs/coolapk_preferences_v7.xml | egrep -o 'USER_SPAM_([0-9]+)' | sed 's/USER_SPAM_//g'"
        return com.drake.net.utils.withDefault {
            ArrayList<String>().apply {
                Shell.cmd(command).to(this).exec()
            }
        }
    }
}