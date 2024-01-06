package com.luckyzyx.luckytool.hook.utils.sysui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveWiFiDataInout.toClass
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.SDK

@Suppress("unused")
class BatteryControllerUtils(val classLoader: ClassLoader?) {

    val clazz = VariousClass(
        "com.oplusos.systemui.keyguard.charginganim.ChargingAnimationImpl", //C12 C13
        "com.oplusos.systemui.common.battery.OplusBatteryController" //C14
    ).toClass(classLoader)

    fun getInstance(context: Context): Any? {
        return if (clazz.hasMethod { name = "getInstance";param(ContextClass) })
            clazz.method { name = "getInstance" }.get().call(context)
        else clazz.method { name = "getInstance" }.get().call()
    }

    fun getChargerTechnology(instance: Any): Int {
        return instance.current().method { name = "getChargerTechnology" }.int()
    }

    fun getChargerWattage(instance: Any): Int {
        return instance.current().method { name = "getChargerWattage" }.int()
    }

    fun getPPSMode(instance: Any): Int {
        return instance.current().field {
            name = if (SDK >= A14) "chargeMode" else "mPPSState"
        }.int()
    }

    fun isWirelessCharging(instance: Any): Boolean {
        return instance.current().method {
            name = if (SDK >= A14) "getWirelessCharging" else "isWirelessCharging"
        }.boolean()
    }

    fun getTechnologyName(
        technology: Int? = 0, ppsMode: Int? = 0, isWireless: Boolean = false
    ): String {
        return when (technology) {
            0 -> when (ppsMode) {
                1 -> "PrivatePPS"
                2 -> "PublicPPS"
                3 -> "PublicUFCS"
                4 -> "PrivateUFCS"
                else -> "Normal"
            }

            1 -> if (isWireless) "AirVOOC" else "VOOC"
            2 -> if (isWireless) "AirSVOOC" else "SUPERVOOC"
            20 -> if (isWireless) "AirSVOOC2" else "SUPERVOOC2.0"
            30 -> if (isWireless) "AirSVOOC" else "SUPERVOOC Athena Foreign Pro"
            25 -> if (isWireless) "AirVOOC" else "VOOC Beta Pro"
            3 -> "PD"
            4 -> "QC"
            5 -> "PPS" //null
            6 -> "UFCS" //null
            else -> "[${technology}]"
        }
    }
}