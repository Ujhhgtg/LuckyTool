package com.luckyzyx.luckytool.hook.utils.sysui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.hook.hookers.HookSystemUILockScreen.toClass

@Obfuscate
@Suppress("unused", "MemberVisibilityCanBePrivate")
class WeatherInfoParseHelper(val classLoader: ClassLoader?) {
    val clazz = VariousClass(
        "com.oplusos.systemui.keyguard.clock.WeatherInfoParseHelper",  //C13
        "com.oplus.systemui.keyguard.clock.WeatherInfoParseHelper"  //C14
    ).toClass(classLoader)
    val holderInnerClazz = VariousClass(
        "com.oplusos.systemui.keyguard.clock.WeatherInfoParseHelper\$HolderInnerClass",  //C13
        "com.oplus.systemui.keyguard.clock.WeatherInfoParseHelper\$HolderInnerClass"  //C14
    ).toClass(classLoader)
    val weatherInfoClazz = VariousClass(
        "com.oplusos.systemui.keyguard.clock.WeatherInfoParseHelper\$WeatherInfo",  //C13
        "com.oplus.systemui.keyguard.clock.WeatherInfoParseHelper\$WeatherInfo"  //C14
    ).toClass(classLoader)
    val timeInfoClazz = VariousClass(
        "com.oplusos.systemui.keyguard.clock.WeatherInfoParseHelper\$TimeInfo",  //C13
        "com.oplus.systemui.keyguard.clock.WeatherInfoParseHelper\$TimeInfo"  //C14
    ).toClass(classLoader)

    fun getInstance(): Any? {
        return holderInnerClazz.field { type = clazz }.get().any()
    }

    fun getLocalTimeInfo(context: Context): Any? {
        return clazz.method {
            name = "getLocalTimeInfo"
            paramCount = 1
        }.get(getInstance()).call(context)
    }

    fun getResidentTimeInfo(context: Context, residentTimeZone: String): Any? {
        return clazz.method {
            name = "getResidentTimeInfo"
            paramCount = 1
        }.get(getInstance()).call(context, residentTimeZone)
    }
}