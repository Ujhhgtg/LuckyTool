package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.joom.paranoid.Obfuscate

@Obfuscate
class OplusCommonFeatureUtils(val classLoader: ClassLoader?) {

    val clazz = "android.common.OplusFeatureCache".toClass(classLoader)

    fun getFeatureCache(cls: Any): Any? {
        return clazz.method { name = "get" }.get().call(cls)
    }

    fun getDefaultFeature(cls: String): Any? {
        val oplusCommonFeature = cls.toClass(classLoader)
        return oplusCommonFeature.field { type = cls }.get().any()
    }

}