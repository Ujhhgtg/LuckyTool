package com.luckyzyx.luckytool.service

import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IRefreshRateController
import com.luckyzyx.luckytool.service.controller.RefreshRateControllerService
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
object RefreshRateService {
    private val TAG = "RefreshRateService"
    private var controller: IRefreshRateController? = null

    fun init(context: Context) {
        get(context) {}
    }

    fun get(context: Context, result: (IRefreshRateController?) -> Unit) {
        if (controller != null) result(controller)
        else  context.bindRootService(RefreshRateControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IRefreshRateController.Stub.asInterface(iBinder)
                LogUtils.d(TAG, "get", "${controller != null}", true)
                result(controller)
            })
    }
}