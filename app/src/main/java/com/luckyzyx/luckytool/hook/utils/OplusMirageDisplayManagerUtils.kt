package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.luckyzyx.luckytool.hook.scopes.android.HookFloatMirageWindow.toClass

class OplusMirageDisplayManagerUtils(val classLoader: ClassLoader?) {

    val service = "com.android.server.display.OplusMirageDisplayManagerService".toClass(classLoader)

    fun getInstance(): Any? {
        return service.method {
            name = "getInstance";emptyParam()
        }.get().call()
    }

    fun notifyCastSuccess(instance: Any, displayId: Int) {
        instance.current().method { name = "notifyCastSuccess";param(IntType) }
            .call(displayId)
    }
}