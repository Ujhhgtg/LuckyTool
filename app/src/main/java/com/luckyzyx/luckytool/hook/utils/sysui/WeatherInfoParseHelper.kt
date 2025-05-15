package com.luckyzyx.luckytool.hook.utils.sysui

import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.hook.hookers.HookSystemUILockScreen.toClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
@Suppress("unused", "MemberVisibilityCanBePrivate")
class WeatherInfoParseHelper(val classLoader: ClassLoader?) {
    val clazz = VariousClass(
        "com.oplusos.systemui.keyguard.clock.WeatherInfoParseHelper",  //C13
        "com.oplus.systemui.keyguard.clock.WeatherInfoParseHelper"  //C14
    ).toClass(classLoader)
    val holderInnerClazz = "${clazz.name}\$HolderInnerClass".toClass(classLoader)
    val weatherInfoClazz = "${clazz.name}\$WeatherInfo".toClass(classLoader)
    val timeInfoClazz = "${clazz.name}\$TimeInfo".toClass(classLoader)

    fun getInstance(): Any? {
        return holderInnerClazz.field { type = clazz }.get().any()
    }

    fun getLocalTimeInfo(context: Context): Any? {
        return clazz.method {
            name = "getLocalTimeInfo"
            param(ContextClass)
        }.get(getInstance()).call(context)
    }

    fun getResidentTimeInfo(context: Context, residentTimeZone: String): Any? {
        return clazz.method {
            name = "getResidentTimeInfo"
            param(ContextClass, StringClass)
        }.get(getInstance()).call(context, residentTimeZone)
    }
}