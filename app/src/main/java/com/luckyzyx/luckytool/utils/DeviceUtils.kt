package com.luckyzyx.luckytool.utils

import com.joom.paranoid.Obfuscate
import com.topjohnwu.superuser.ShellUtils

@Obfuscate
class DeviceUtils {
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

}