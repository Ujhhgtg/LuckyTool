package com.luckyzyx.luckytool.hook.utils.sysui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.hook.hookers.HookLockScreen.toClass

@Suppress("unused", "MemberVisibilityCanBePrivate")
class ClockSwitchHelper(val classLoader: ClassLoader?) {

    val clazz = VariousClass(
        "com.oplusos.systemui.keyguard.clock.ClockSwitchHelper",  //C12 C13
        "com.oplus.systemui.keyguard.clock.ClockSwitchHelper"  //C14
    ).toClass(classLoader)

    fun getInstance(context: Context): Any? {
        return clazz.method {
            name = "getInstance"
            paramCount = 1
        }.get().call(context)
    }

    fun getLocatedWeatherInfo(instance: Any): Any? {
        return clazz.method {
            name = "getLocatedWeatherInfo"
            emptyParam()
        }.get(instance).call()
    }

    fun getResidentWeatherInfo(instance: Any): Any? {
        return clazz.method {
            name = "getResidentWeatherInfo"
            emptyParam()
        }.get(instance).call()
    }
}