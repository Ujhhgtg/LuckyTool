package com.luckyzyx.luckytool.hook.utils

import android.os.ServiceManager
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.IBinderClass
import com.luckyzyx.luckytool.hook.hookers.HookAndroid.toClass


@Suppress("PrivatePropertyName")
class IChargerUtils(val classLoader: ClassLoader?) {

    private val CLASS_OPLUS_CHARGER = "vendor.oplus.hardware.charger.V1_0.ICharger" //C12 C13
    private val CLASS_OPLUS_CHARGER_NEW = "vendor.oplus.hardware.charger.ICharger" //C14 C15
    private val CHARGER_STUB_CLASS = "vendor.oplus.hardware.charger.ICharger\$Stub"
    private val CHARGER_SERVICE_NAME = "vendor.oplus.hardware.charger.ICharger/default"

    val clazz = VariousClass(CLASS_OPLUS_CHARGER, CLASS_OPLUS_CHARGER_NEW).toClass(classLoader)

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