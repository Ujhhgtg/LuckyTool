package com.luckyzyx.luckytool.hook.utils

import android.os.ServiceManager
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.IBinderClass
import com.luckyzyx.luckytool.hook.hookers.HookAndroid.toClass


@Suppress("PrivatePropertyName")
class IChargerUtils(val classLoader: ClassLoader?) {

    val clazz = VariousClass(
        "vendor.oplus.hardware.charger.V1_0.ICharger",  //C12 C13
        "vendor.oplus.hardware.charger.ICharger" //C14
    ).toClass(classLoader)

    private val CHARGER_STUB_CLASS = "vendor.oplus.hardware.charger.ICharger\$Stub"
    private val CHARGER_SERVICE_NAME = "vendor.oplus.hardware.charger.ICharger/default"
    private val CLASS_OPLUS_CHARGER = "vendor.oplus.hardware.charger.V1_0.ICharger"

    fun getInstance(): Any? {
        return if (clazz.name == CLASS_OPLUS_CHARGER) {
            clazz.method { name = "getService";emptyParam() }.get().call()
        } else {
            val service = ServiceManager.getService(CHARGER_SERVICE_NAME)
            CHARGER_STUB_CLASS.toClass(classLoader).method {
                name = "asInterface";param(IBinderClass)
            }.get().call(service)
        }
    }

    fun queryChargeInfo(ins: Any?): String? {
        return clazz.method {
            name = "queryChargeInfo";emptyParam()
        }.get(ins).invoke<String>()
    }
}