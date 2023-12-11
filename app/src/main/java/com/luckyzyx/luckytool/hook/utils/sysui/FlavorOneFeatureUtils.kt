package com.luckyzyx.luckytool.hook.utils.sysui

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.SDK

@Suppress("unused")
class FlavorOneFeatureUtils(val classLoader: ClassLoader?) {

    val clazz = "com.oplusos.systemui.common.feature.FlavorOneFeatureOption".toClass(classLoader)

    fun getInstance(): Any? {
        return if (SDK >= A14) clazz.field { name = "INSTANCE" }.get().any() else null
    }

    fun isFlavorOneDevice(): Boolean? {
        return clazz.method { name = "isFlavorOneDevice" }.get(getInstance()).invoke<Boolean>()
    }
}