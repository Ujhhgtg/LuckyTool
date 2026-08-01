package com.luckyzyx.luckytool.service.base

import android.content.ComponentName
import android.content.Context
import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.UserHandle
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.bindRootService

@Suppress("PropertyName")
abstract class BaseControllerService<T : IInterface> {
    abstract val TAG: String
    abstract var controllerService: Class<*>
    open var controller: T? = null

    open fun init(context: Context) {
        get(context) {}
    }

    abstract fun getController(iBinder: IBinder?): T?

    open fun get(context: Context?, result: (T?) -> Unit) {
        if (context == null || controller != null) result(controller)
        else context.bindRootService(controllerService, { _: ComponentName?, iBinder: IBinder? ->
            controller = getController(iBinder)

            val uid = Binder.getCallingUid()
            val userid = UserHandle::class.asResolver().firstMethod {
                name = "getUserId"
                parameters(Int::class)
            }.invoke(Binder.getCallingUid())
            LogUtils.d(
                TAG, "get ($uid : $userid)", "${controller != null}", true
            )
            result(controller)
        })
    }
}