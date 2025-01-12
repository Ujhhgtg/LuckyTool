package com.luckyzyx.luckytool.service

import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.ITileServiceController
import com.luckyzyx.luckytool.service.controller.TileControllerService
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.bindRootService

@Obfuscate
object TilesService {
    private val TAG = "TileService"
    private var controller: ITileServiceController? = null

    fun init(context: Context) {
        get(context) {}
    }

    fun get(context: Context, result: (ITileServiceController?) -> Unit) {
        if (controller != null) result(controller)
        else context.bindRootService(TileControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = ITileServiceController.Stub.asInterface(iBinder)
                LogUtils.d(TAG, "get", "${controller != null}", true)
                result(controller)
            })
    }
}