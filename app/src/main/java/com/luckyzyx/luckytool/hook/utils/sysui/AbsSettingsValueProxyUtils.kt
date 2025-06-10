package com.luckyzyx.luckytool.hook.utils.sysui

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class AbsSettingsValueProxyUtils(val classLoader: ClassLoader?) {

    val clazz = "com.oplusos.systemui.common.settingsvalue.AbsSettingsValueProxy"
        .toClass(classLoader)

    fun getGlobalIntValue(context: Context, key: String, defValue: Int): Int {
        return clazz.method {
            name = "getGlobalIntValue"
            param(ContextClass, StringClass, IntType)
            returnType = IntType
        }.get().invoke<Int>(context, key, defValue) ?: defValue
    }

    fun getSecureIntValue(context: Context, key: String, defValue: Int): Int {
        return clazz.method {
            name = "getSecureIntValue"
            param(ContextClass, StringClass, IntType)
            returnType = IntType
        }.get().invoke<Int>(context, key, defValue) ?: defValue
    }

    fun getSecureIntValue(context: Context, key: String, defValue: Int, userId: Int): Int {
        return clazz.method {
            name = "getSecureIntValue"
            param(ContextClass, StringClass, IntType)
            returnType = IntType
        }.get().invoke<Int>(context, key, defValue, userId) ?: defValue
    }

    fun getSecureStringValue(context: Context, key: String): String {
        return clazz.method {
            name = "getSecureStringValue"
            param(ContextClass, StringClass)
            returnType = StringClass
        }.get().invoke<String>(context, key) ?: ""
    }

    fun getSecureStringValue(context: Context, key: String, userId: Int): String {
        return clazz.method {
            name = "getSecureStringValue"
            param(ContextClass, StringClass, IntType)
            returnType = StringClass
        }.get().invoke<String>(context, key, userId) ?: ""
    }

    fun getSystemFloatValue(context: Context, key: String, defValue: Float): Float {
        return clazz.method {
            name = "getSystemFloatValue"
            param(ContextClass, StringClass, FloatType)
            returnType = FloatType
        }.get().invoke<Float>(context, key, defValue) ?: defValue
    }

    fun getSystemIntValue(context: Context, key: String, defValue: Int): Int {
        return clazz.method {
            name = "getSystemIntValue"
            param(ContextClass, StringClass, IntType)
            returnType = IntType
        }.get().invoke<Int>(context, key, defValue) ?: defValue
    }

    fun getSystemIntValue(context: Context, key: String, defValue: Int, userId: Int): Int {
        return clazz.method {
            name = "getSystemIntValue"
            param(ContextClass, StringClass, IntType, IntType)
            returnType = IntType
        }.get().invoke<Int>(context, key, defValue, userId) ?: defValue
    }

    fun getSystemStringValue(context: Context, key: String): String {
        return clazz.method {
            name = "getSystemStringValue"
            param(ContextClass, StringClass)
            returnType = StringClass
        }.get().invoke<String>(context, key) ?: ""
    }

    fun getSystemStringValue(context: Context, key: String, userId: Int): String {
        return clazz.method {
            name = "getSystemStringValue"
            param(ContextClass, StringClass, IntType)
            returnType = StringClass
        }.get().invoke<String>(context, key, userId) ?: ""
    }

}