package com.luckyzyx.luckytool.hook.utils.sysui

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class WifiUtils(val classLoader: ClassLoader?) {

    val clazz = "com.oplus.systemui.statusbar.util.WifiUtils".toClass(classLoader)

    fun getInstance(): Any? {
        return clazz.field { name = "INSTANCE" }.get().any()
    }

    fun isDualWifiConnected(context: Context): Boolean {
        return clazz.method { name = "isDualWifiConnected" }.get(getInstance()).boolean(context)
    }

    fun isPassPointAp(context: Context): Boolean {
        return clazz.method { name = "isPassPointAp" }.get(getInstance()).boolean(context)
    }

}