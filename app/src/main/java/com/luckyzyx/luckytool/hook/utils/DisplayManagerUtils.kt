package com.luckyzyx.luckytool.hook.utils

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import android.view.DisplayAddress
import android.view.DisplayInfo
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass


@Suppress("unused", "MemberVisibilityCanBePrivate")
class DisplayManagerUtils(val classLoader: ClassLoader?) {

    val clazz = "android.hardware.display.DisplayManager".toClass(classLoader)
    val displayClazz = "android.view.Display".toClass(classLoader)
    val displayInfoClazz = "android.view.DisplayInfo".toClass(classLoader)
    val addressPhysicalClazz = "android.view.DisplayAddress\$Physical".toClass(classLoader)

    fun getDisplayManagerService(context: Context): DisplayManager {
        return context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    fun Display.getDisplayInfo(outDisplayInfo: DisplayInfo?): Boolean {
        return displayClazz.method {
            name = "getDisplayInfo"
            param(displayInfoClazz)
        }.get(this).invoke<Boolean>(outDisplayInfo) ?: false
    }

    fun getDynamicDisplayInfo(displayInfo: DisplayInfo): Any? {
        return if (displayInfo.address is DisplayAddress.Physical) {
            val physicalDisplayId =
                (displayInfo.address as DisplayAddress.Physical).physicalDisplayId
            SurfaceControlUtils(classLoader).let {
                if (it.isDisplayToken()) {
                    val token = it.getPhysicalDisplayToken(physicalDisplayId)
                    it.getDynamicDisplayInfo(token)
                } else {
                    it.getDynamicDisplayInfo(physicalDisplayId)
                }
            }
        } else {
            SurfaceControlUtils(classLoader).let {
                if (it.isDisplayToken()) {
                    val token = it.getInternalDisplayToken()
                    it.getDynamicDisplayInfo(token)
                } else {
                    it.getDynamicDisplayInfo(0)
                }
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