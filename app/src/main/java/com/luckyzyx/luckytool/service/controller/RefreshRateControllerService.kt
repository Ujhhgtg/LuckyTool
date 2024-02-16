package com.luckyzyx.luckytool.service.controller

import android.content.Intent
import android.os.Parcel
import android.os.RemoteException
import android.os.ServiceManager
import android.view.DisplayInfo
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IRefreshRateController
import com.luckyzyx.luckytool.data.DisplayMode
import com.luckyzyx.luckytool.hook.utils.DisplayManagerUtils
import com.luckyzyx.luckytool.hook.utils.DynamicDisplayInfoUtils
import com.luckyzyx.luckytool.utils.LogUtils
import com.topjohnwu.superuser.ipc.RootService

@Obfuscate
class RefreshRateControllerService : RootService() {
    val tag = "RefreshRateControllerService"
    val isDebug = true

    companion object {
        private const val serviceName = "SurfaceFlinger"
        private const val interfaceName = "android.ui.ISurfaceComposer"

        private val surfaceFlinger by lazy {
            ServiceManager.getService(serviceName)
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
                    val displayManager = getDisplayManagerService(context)
                    LogUtils.d(tag, "getSupportModes", "${displayManager.javaClass}", isDebug)
                    val display = displayManager.getDisplay(0)
                    LogUtils.d(tag, "getSupportModes", "${display.javaClass}", isDebug)
                    val displayInfo = DisplayInfo()
                    if (!display.getDisplayInfo(displayInfo)) return list
                    LogUtils.d(tag, "getSupportModes", "getDisplayInfo true", isDebug)
                    val dynamicInfo = getDynamicDisplayInfo(displayInfo) ?: return list
                    LogUtils.d(tag, "getSupportModes", "${dynamicInfo.javaClass}", isDebug)
                    DynamicDisplayInfoUtils(dynamicInfo).apply {
                        val allDisplayModes = getSupportedDisplayModes()
                        LogUtils.d(tag, "getSupportModes", "${allDisplayModes.toList()}", isDebug)
                        allDisplayModes.forEach {
                            LogUtils.d(tag, "getSupportModes", "Mode $it", isDebug)
                            val mode = getDisplayMode(it) ?: return@forEach
                            list.add(mode.first, mode.second)
                            LogUtils.d(tag, "getSupportModes", "Mode is add", isDebug)
                        }
                    }
                }
                LogUtils.d(tag, "getSupportModes", "Final size ${list.size}", isDebug)
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
                if (modeId >= 0) LogUtils.e(tag, "setRefreshRateMode", "$e", true)
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