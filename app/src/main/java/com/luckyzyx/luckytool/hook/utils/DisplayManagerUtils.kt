package com.luckyzyx.luckytool.hook.utils

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.extends
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.hook.scope.systemui.FingerPrintIconAnim.toClass


@Suppress("unused", "MemberVisibilityCanBePrivate")
class DisplayManagerUtils(val classLoader: ClassLoader?) {

    val clazz = "android.hardware.display.DisplayManager".toClass(classLoader)
    val displayClazz = "android.view.Display".toClass(classLoader)
    val displayInfoClazz = "android.view.DisplayInfo".toClass(classLoader)
    val addressPhysicalClazz = "android.view.DisplayAddress\$Physical".toClass(classLoader)

    fun getService(context: Context): DisplayManager {
        return context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    fun Display.getDisplayInfo(outDisplayInfo: Any?): Boolean? {
        return displayClazz.method {
            name = "getDisplayInfo"
            param(displayInfoClazz)
        }.get(this).invoke<Boolean>(outDisplayInfo)
    }

    fun getDynamicDisplayInfo(displayInfo: Any): Any? {
        val address = displayInfo.current().field { name = "address" }.any() ?: return null
        val extend = address.javaClass extends addressPhysicalClazz
        val physicalDisplayId = getPhysicalDisplayId(address)
        return SurfaceControlUtils(classLoader).let {
            if (it.isDisplayToken()) {
                val token = if (extend) it.getPhysicalDisplayToken(physicalDisplayId)
                else it.getInternalDisplayToken()
                it.getDynamicDisplayInfo(token)
            } else {
                val id = if (extend) physicalDisplayId else 0
                it.getDynamicDisplayInfo(id)
            }
        }
    }

    fun getPhysicalDisplayId(address: Any): Long? {
        return address.current().method {
            name = "getPhysicalDisplayId"
            emptyParam()
        }.invoke<Long>()
    }
}