package com.luckyzyx.luckytool.hook.utils

import android.content.ContentResolver
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass

@Suppress("unused", "PropertyName")
class SettingsUtils(val classLoader: ClassLoader?) {

    val clazz = "android.provider.Settings".toClass(classLoader)

    val Global = "android.provider.Settings\$Global".toClass(classLoader)
    val Secure = "android.provider.Settings\$Secure".toClass(classLoader)
    val System = "android.provider.Settings\$System".toClass(classLoader)

    fun getIntForUser(type: Class<*>, cr: ContentResolver, key: String, userHandle: Int): Int? {
        return type.method {
            name = "getIntForUser"
            paramCount = 3
        }.get().invoke<Int>(cr, key, userHandle)
    }

    fun getIntForUser(
        type: Class<*>, cr: ContentResolver, key: String, def: Int, userHandle: Int
    ): Int? {
        return type.method {
            name = "getIntForUser"
            paramCount = 4
        }.get().invoke<Int>(cr, key, def, userHandle)
    }

    fun getLongForUser(
        type: Class<*>, cr: ContentResolver, key: String, userHandle: Int
    ): Long? {
        return type.method {
            name = "getLongForUser"
            paramCount = 3
        }.get().invoke<Long>(cr, key, userHandle)
    }

    fun getLongForUser(
        type: Class<*>, cr: ContentResolver, key: String, def: Long, userHandle: Int
    ): Long? {
        return type.method {
            name = "getLongForUser"
            paramCount = 4
        }.get().invoke<Long>(cr, key, def, userHandle)
    }

    fun getFloatForUser(
        type: Class<*>, cr: ContentResolver, key: String, userHandle: Int
    ): Float? {
        return type.method {
            name = "getFloatForUser"
            paramCount = 3
        }.get().invoke<Float>(cr, key, userHandle)
    }

    fun getFloatForUser(
        type: Class<*>, cr: ContentResolver, key: String, def: Float, userHandle: Int
    ): Float? {
        return type.method {
            name = "getFloatForUser"
            paramCount = 4
        }.get().invoke<Float>(cr, key, def, userHandle)
    }

    fun getStringForUser(
        type: Class<*>, cr: ContentResolver, key: String, userHandle: Int
    ): String? {
        return type.method {
            name = "getStringForUser"
            paramCount = 3
        }.get().invoke<String>(cr, key, userHandle)
    }

    fun putIntForUser(
        type: Class<*>, cr: ContentResolver, key: String, value: Int, userHandle: Int
    ): Boolean? {
        return type.method {
            name = "putIntForUser"
            paramCount = 4
        }.get().invoke<Boolean>(cr, key, value, userHandle)
    }

    fun putLongForUser(
        type: Class<*>, cr: ContentResolver, key: String, value: Long, userHandle: Int
    ): Boolean? {
        return type.method {
            name = "putLongForUser"
            paramCount = 4
        }.get().invoke<Boolean>(cr, key, value, userHandle)
    }

    fun putFloatForUser(
        type: Class<*>, cr: ContentResolver, key: String, value: Float, userHandle: Int
    ): Boolean? {
        return type.method {
            name = "putFloatForUser"
            paramCount = 4
        }.get().invoke<Boolean>(cr, key, value, userHandle)
    }

    fun putString(
        type: Class<*>, cr: ContentResolver, key: String, value: String,
        overrideableByRestore: Boolean
    ): Boolean? {
        return type.method {
            name = "putString"
            paramCount = 4
        }.get().invoke<Boolean>(cr, key, value, overrideableByRestore)
    }

    fun putStringForUser(
        type: Class<*>, cr: ContentResolver, key: String, value: String, userHandle: Int
    ): Boolean? {
        return type.method {
            name = "putStringForUser"
            paramCount = 4
        }.get().invoke<Boolean>(cr, key, value, userHandle)
    }

    fun putStringForUser(
        type: Class<*>, cr: ContentResolver, key: String, value: String, userHandle: Int,
        overrideableByRestore: Boolean
    ): Boolean? {
        return type.method {
            name = "putStringForUser"
            paramCount = 5
        }.get().invoke<Boolean>(cr, key, value, userHandle, overrideableByRestore)
    }

    fun putStringForUser(
        type: Class<*>, cr: ContentResolver, key: String, value: String, tag: String,
        makeDefault: Boolean, userHandle: Int, overrideableByRestore: Boolean
    ): Boolean? {
        return type.method {
            name = "putStringForUser"
            paramCount = 7
        }.get().invoke<Boolean>(cr, key, value, tag, makeDefault, userHandle, overrideableByRestore)
    }
}