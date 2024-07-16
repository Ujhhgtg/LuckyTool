package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass

class OplusFeatureCacheUtils(val classLoader: ClassLoader?) {

    val clazz = "android.common.OplusFeatureCache".toClass(classLoader)

    fun get(cls: Any): Any? {
        return clazz.method { name = "get" }.get().call(cls)
    }

}