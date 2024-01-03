package com.luckyzyx.luckytool.utils

import org.json.JSONObject
import java.io.File

@Suppress("MemberVisibilityCanBePrivate", "unused")
object DevicesConfigUtils {

    private var deviceConfigFile: File? = null
    private var deviceConfig: JSONObject? = null

    fun getConfigJson(): JSONObject? {
        val file = File("/odm/etc/devices_config/devices_config.json")
        if (deviceConfigFile == null) deviceConfigFile = if (file.exists()) file else null
        if (deviceConfig == null) deviceConfigFile?.readText()?.let { JSONObject(it) }
        return deviceConfig
    }

    fun getChargeConfig(): JSONObject? {
        return getConfigJson()?.getJSONObject("charge")
    }

    fun getOTGConfig(): JSONObject? {
        return getConfigJson()?.getJSONObject("otg")
    }

    fun getShouderConfig(): JSONObject? {
        return getConfigJson()?.getJSONObject("shouder")
    }

    fun getUSBConfig(): JSONObject? {
        return getConfigJson()?.getJSONObject("usb")
    }

    fun getVibratorConfig(): JSONObject? {
        return getConfigJson()?.getJSONObject("vibrator")
    }

    fun getWhiteLightConfig(): JSONObject? {
        return getConfigJson()?.getJSONObject("lights_white")
    }

    fun getWirelessChargeConfig(): JSONObject? {
        return getConfigJson()?.getJSONObject("wireless_charge")
    }

    /**
     * 是否为串联电池
     */
    val isSeriesDualBattery
        get() : Boolean? = safeOfNull {
            getChargeConfig()?.getBoolean("series_dual_battery_support")
        }

    /**
     * 是否为并联电池
     */
    val isParallelDualBattery
        get() : Boolean? = safeOfNull {
            getChargeConfig()?.getBoolean("parallel_dual_battery_support")
        }

    val isVBatDeviation
        get() : Boolean? = safeOfNull {
            getChargeConfig()?.getBoolean("qg_vbat_deviation_support")
        }

    val isAirSVOOCSupport
        get() = safeOfNull {
            getWirelessChargeConfig()?.getBoolean("air_svooc_support")
        }

    val isAirVOOCSupport
        get() = safeOfNull {
            getWirelessChargeConfig()?.getBoolean("air_vooc_support")
        }
}