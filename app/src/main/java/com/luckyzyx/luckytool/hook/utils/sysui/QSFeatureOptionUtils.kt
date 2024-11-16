package com.luckyzyx.luckytool.hook.utils.sysui

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import com.joom.paranoid.Obfuscate

@Obfuscate
@Suppress("unused")
class QSFeatureOptionUtils(val classLoader: ClassLoader?) {

    val clazz = "com.oplusos.systemui.common.feature.QSFeatureOption".toClassOrNull(classLoader)

    fun getInstance(): Any? {
        return clazz?.field { name = "INSTANCE" }?.get()?.any()
    }

    fun isSupportVolumeSeekBar(): Boolean {
        return if (clazz == null || getInstance() == null) false
        else if (clazz.hasMethod { name = "isSupportVolumeSeekBar" }) {
            clazz.method { name = "isSupportVolumeSeekBar" }.get(getInstance())
                .invoke<Boolean>() ?: false
        } else false
    }
}