package com.luckyzyx.luckytool.ui.service

import android.content.Intent
import android.os.Parcel
import android.os.RemoteException
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.luckyzyx.luckytool.IRefreshRateController
import com.luckyzyx.luckytool.hook.utils.DisplayManagerUtils
import com.luckyzyx.luckytool.hook.utils.ServiceManagerUtils
import com.luckyzyx.luckytool.utils.DisplayMode
import com.luckyzyx.luckytool.utils.LogUtils
import com.topjohnwu.superuser.ipc.RootService

class RefreshRateControllerService : RootService() {
    val tag = "RefreshRateControllerService"
    val isDebug = false

    companion object {
        private const val serviceName = "SurfaceFlinger"
        private const val interfaceName = "android.ui.ISurfaceComposer"

        private val surfaceFlinger by lazy {
            ServiceManagerUtils(null).getService(serviceName)
        }
    }

    override fun onBind(intent: Intent) = object : IRefreshRateController.Stub() {
        override fun getRefreshRateDisplay(): Boolean {
            try {
                if (surfaceFlinger == null) surfaceFlinger
                if (surfaceFlinger != null) {
                    val obtain = Parcel.obtain()
                    val obtain2 = Parcel.obtain()
                    obtain.writeInterfaceToken(interfaceName)
                    obtain.writeInt(2)
                    surfaceFlinger?.transact(1034, obtain, obtain2, 0)
                    val status = obtain2.readBoolean()
                    obtain2.recycle()
                    obtain.recycle()
                    return status
                }
                LogUtils.d(tag, "getRefreshRateDisplay", "surfaceFlinger is null", isDebug)
                return false
            } catch (e: RemoteException) {
                LogUtils.e(tag, "getRefreshRateDisplay ", "$e", true)
                return false
            }
        }

        override fun setRefreshRateDisplay(status: Boolean) {
            try {
                if (surfaceFlinger == null) surfaceFlinger
                if (surfaceFlinger != null) {
                    val obtain = Parcel.obtain()
                    obtain.writeInterfaceToken(interfaceName)
                    obtain.writeInt(if (status) 1 else 0)
                    surfaceFlinger?.transact(1034, obtain, null, 0)
                    obtain.recycle()
                    return
                }
                LogUtils.d(tag, "setRefreshRateDisplay", "surfaceFlinger is null", isDebug)
            } catch (e: RemoteException) {
                LogUtils.e(tag, "setRefreshRateDisplay ", "$e", true)
            }
        }

        override fun getSupportModes(): ArrayList<DisplayMode> {
            val list = ArrayList<DisplayMode>()
            val context = this@RefreshRateControllerService
            return try {
                DisplayManagerUtils(null).apply {
                    val displayManager = getService(context)
                    LogUtils.d(tag, "getSupportModes", "${displayManager.javaClass}", isDebug)
                    val display = displayManager.getDisplay(0)
                    LogUtils.d(tag, "getSupportModes", "${display.javaClass}", isDebug)
                    val displayInfo = displayInfoClazz.buildOf { emptyParam() } ?: return list
                    LogUtils.d(tag, "getSupportModes", "${displayInfo.javaClass}", isDebug)
                    if (display.getDisplayInfo(displayInfo) != true) return list
                    LogUtils.d(tag, "getSupportModes", "getDisplayInfo true", isDebug)
                    val dynamicInfo = getDynamicDisplayInfo(displayInfo)
                    LogUtils.d(tag, "getSupportModes", "${dynamicInfo?.javaClass}", isDebug)
                    val supportedDisplayModes = dynamicInfo?.current()?.field {
                        name = "supportedDisplayModes"
                    }?.array<Any>()
                    LogUtils.d(
                        tag,
                        "getSupportModes",
                        "AllMode ${supportedDisplayModes?.toList()}",
                        isDebug
                    )
                    supportedDisplayModes?.forEach {
                        LogUtils.d(tag, "getSupportModes", "Mode $it", isDebug)
                        val id = it.current().field { name = "id" }.cast<Int>() ?: return@forEach
                        val width = it.current().field { name = "width" }.cast<Int>()
                        val height = it.current().field { name = "height" }.cast<Int>()
                        val xDpi = it.current().field { name = "xDpi" }.cast<Float>()
                        val yDpi = it.current().field { name = "yDpi" }.cast<Float>()
                        val refreshRate = it.current().field { name = "refreshRate" }.cast<Float>()
                        val appVsyncOffsetNanos = it.current().field {
                            name = "appVsyncOffsetNanos"
                        }.cast<Long>()
                        val presentationDeadlineNanos = it.current().field {
                            name = "presentationDeadlineNanos"
                        }.cast<Long>()
                        val group = it.current().field { name = "group" }.cast<Int>()
                        val mode = DisplayMode(
                            id, width, height, xDpi, yDpi,
                            refreshRate, appVsyncOffsetNanos, presentationDeadlineNanos, group
                        )
                        list.add(id, mode)
                        LogUtils.d(tag, "getSupportModes", "Mode is add", isDebug)
                    }
                }
                LogUtils.d(tag, "getSupportModes", "Size ${list.size}", isDebug)
                list
            } catch (e: Exception) {
                LogUtils.e(tag, "getSupportModes", "$e", true)
                list
            }
        }

        override fun setRefreshRateMode(modeId: Int) {
            try {
                if (surfaceFlinger == null) surfaceFlinger
                if (surfaceFlinger != null) {
                    val obtain = Parcel.obtain()
                    obtain.writeInterfaceToken(interfaceName)
                    obtain.writeInt(modeId)
                    surfaceFlinger?.transact(1035, obtain, null, 0)
                    obtain.recycle()
                    return
                }
                LogUtils.d(tag, "setRefreshRateMode", "surfaceFlinger is null", isDebug)
            } catch (e: Exception) {
                LogUtils.e(tag, "setRefreshRateMode", "$e", true)
            }
        }

        override fun resetRefreshRateMode() {
            try {
                setRefreshRateMode(-1)
            } catch (e: Exception) {
//                LogUtils.e(tag, "resetRefreshRateMode", "$e", true)
            }
        }
    }
}