package com.luckyzyx.luckytool.hook.utils.sysui

import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DependencyUtils(val classLoader: ClassLoader?, isEx: Boolean = false) {

    val clazz = if (isEx) "com.android.systemui.DependencyEx".toClass(classLoader)
    else "com.android.systemui.Dependency".toClass(classLoader)

    fun get(cls: Class<*>): Any? {
        return clazz.method {
            name = "get"
            param(Class::class.java)
        }.get().invoke<Any>(cls)
    }

}