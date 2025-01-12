package com.luckyzyx.luckytool.service

import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IAdbDebugController
import com.luckyzyx.luckytool.service.controller.AdbDebugControllerService
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
object AdbService {
    private val TAG = "AdbService"
    private var controller: IAdbDebugController? = null

    fun init(context: Context) {
        get(context) {}
    }

    fun get(context: Context, result: (IAdbDebugController?) -> Unit) {
        if (controller != null) result(controller)
        else context.bindRootService(AdbDebugControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IAdbDebugController.Stub.asInterface(iBinder)
                LogUtils.d(TAG, "get", "${controller != null}", true)
                result(controller)
            })
    }
}