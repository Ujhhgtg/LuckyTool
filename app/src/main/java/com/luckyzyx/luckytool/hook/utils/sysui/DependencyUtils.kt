package com.luckyzyx.luckytool.hook.utils.sysui

import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.toClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class DependencyUtils(val classLoader: ClassLoader?, isEx: Boolean = false) {

    val clazz = if (isEx) "com.android.systemui.DependencyEx".toClass(classLoader)
    else "com.android.systemui.Dependency".toClass(classLoader)

    fun getDependency(cls: Class<*>): Any? {
        val sDependency = clazz.field { type = clazz }.get().any() ?: return null
        return sDependency.current().method {
            name = "getDependency"
            param(Class::class.java)
        }.invoke<Any>(cls)
    }

}