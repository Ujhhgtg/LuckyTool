package com.luckyzyx.luckytool.hook.utils

import android.os.IBinder
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.type.android.IBinderClass
import com.highcapable.yukihookapi.hook.type.java.LongType

@Suppress("unused")
class SurfaceControlUtils(val classLoader: ClassLoader?) {

    val clazz = "android.view.SurfaceControl".toClass(classLoader)

    fun isDisplayToken(): Boolean {
        return clazz.hasMethod { name = "getDynamicDisplayInfo";param(IBinderClass) }
    }

    fun getDynamicDisplayInfo(displayId: Long?): Any? {
        return clazz.method { name = "getDynamicDisplayInfo";param(LongType) }.get()
            .call(displayId)
    }

    fun getDynamicDisplayInfo(displayToken: IBinder?): Any? {
        return clazz.method { name = "getDynamicDisplayInfo";param(IBinderClass) }.get()
            .call(displayToken)
    }

    fun getPhysicalDisplayToken(physicalDisplayId: Long?): IBinder? {
        return clazz.method { name = "getPhysicalDisplayToken";param(LongType) }.get()
            .invoke<IBinder>(physicalDisplayId)
    }

    fun getInternalDisplayToken(): IBinder? {
        return clazz.method { name = "getInternalDisplayToken";emptyParam() }.get()
            .invoke<IBinder>()
    }
}