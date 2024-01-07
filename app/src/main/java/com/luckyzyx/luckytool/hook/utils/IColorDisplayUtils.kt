package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass

@Suppress("unused", "MemberVisibilityCanBePrivate")
class IColorDisplayUtils(val classLoader: ClassLoader?) {

    val serviceName = "color_display"
    val clazz = "android.hardware.display.ColorDisplayManager".toClass(classLoader)
    val internal = "android.hardware.display.ColorDisplayManager\$ColorDisplayManagerInternal"
        .toClass(classLoader)

    fun getInstance(): Any? {
        return internal.method {
            name = "getInstance"
            emptyParam()
        }.get().call()
    }
}