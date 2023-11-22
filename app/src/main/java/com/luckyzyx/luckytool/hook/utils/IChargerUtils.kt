package com.luckyzyx.luckytool.hook.utils

import android.os.IBinder
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.IBinderClass
import com.luckyzyx.luckytool.hook.scope.systemui.StatusBarBatteryInfoNotify.toClass

@Suppress("MemberVisibilityCanBePrivate", "unused")
class IChargerUtils(val classLoader: ClassLoader?) {

    val clazz = VariousClass(
        "vendor.oplus.hardware.charger.V1_0.ICharger",  //C13
        "vendor.oplus.hardware.charger.ICharger" //C14
    ).toClass(classLoader)

    val stub = "${clazz.canonicalName}\$Stub".toClass(classLoader)
    val serviceName = "vendor.oplus.hardware.charger.ICharger/default"

    private fun getService(): IBinder? {
        return ServiceManagerUtils(classLoader).getService(serviceName)
    }

    fun getInstance(): Any? {
        return if (clazz.hasMethod { name = "getService";emptyParam() }) {
            clazz.method { name = "getService";emptyParam() }.get().call()
        } else stub.method { name = "asInterface";param(IBinderClass) }.get().call(getService())
    }

    fun queryChargeInfo(ins: Any?): String? {
        return clazz.method {
            name = "queryChargeInfo";emptyParam()
        }.get(ins).invoke<String>()
    }

    fun getUIsohValue(ins: Any?): Int? {
        return clazz.method {
            name = "getUIsohValue";emptyParam()
        }.get(ins).invoke<Int>()
    }
}