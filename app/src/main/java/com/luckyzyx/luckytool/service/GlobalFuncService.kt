package com.luckyzyx.luckytool.service

import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IGlobalFuncController
import com.luckyzyx.luckytool.service.controller.GlobalFuncControllerService
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
object GlobalFuncService {
    private val TAG = "GlobalFuncService"
    private var controller: IGlobalFuncController? = null

    fun init(context: Context) {
        get(context) {}
    }

    fun get(context: Context, result: (IGlobalFuncController?) -> Unit) {
        if (controller != null) result(controller)
        else context.bindRootService(GlobalFuncControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IGlobalFuncController.Stub.asInterface(iBinder)
                LogUtils.d(TAG, "get", "${controller != null}", true)
                result(controller)
            })
    }
}