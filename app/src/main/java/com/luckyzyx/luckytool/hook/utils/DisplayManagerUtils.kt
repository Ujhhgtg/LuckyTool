package com.luckyzyx.luckytool.hook.utils

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import android.view.DisplayAddress
import android.view.DisplayInfo
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.toClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
@Suppress("MemberVisibilityCanBePrivate")
class DisplayManagerUtils(val classLoader: ClassLoader?) {

    val clazz = "android.hardware.display.DisplayManager".toClass(classLoader)
    val displayInfoClazz = "android.view.DisplayInfo".toClass(classLoader)

    fun getDisplayManagerService(context: Context): DisplayManager {
        return context.getSystemService(DisplayManager::class.java)
    }

    fun Display.getDisplayInfo(outDisplayInfo: DisplayInfo?): Boolean {
        return resolve().firstMethod {
            name = "getDisplayInfo"
            parameters(displayInfoClazz)
        }.invoke<Boolean>(outDisplayInfo) ?: false
    }

    fun getDynamicDisplayInfo(displayInfo: DisplayInfo): Any? {
        return if (displayInfo.address is DisplayAddress.Physical) {
            val physicalDisplayId = (displayInfo.address as DisplayAddress.Physical)
                .physicalDisplayId
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
        return address.resolve().firstMethod {
            name = "getPhysicalDisplayId"
            emptyParameters()
        }.invoke<Long>()
    }
}