@file:Suppress("DEPRECATION", "WorldReadableFiles", "ApplySharedPref", "UseKtx", "unused")

package com.luckyzyx.luckytool.utils

import android.content.Context
import android.util.ArrayMap
import android.util.ArraySet
import androidx.collection.arrayMapOf
import androidx.collection.arraySetOf

const val ModulePrefs: String = "ModulePrefs"
const val IntentPrefs: String = "IntentPrefs"
const val SettingsPrefs: String = "SettingsPrefs"
const val OtherPrefs: String = "OtherPrefs"

fun Context.getString(prefsName: String, key: String, defaultValue: String = ""): String {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.getString(key, defaultValue) ?: defaultValue
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "getString $key -> $defaultValue", "$t", true)
        defaultValue
    }
}

fun Context.putString(prefsName: String, key: String, value: String): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.edit().putString(key, value).commit()
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "putString $key -> $value", "$t", true)
        false
    }
}

fun Context.getStringSet(
    prefsName: String, key: String, defaultValue: Set<String> = arraySetOf()
): Set<String> {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        ArraySet(prefs.getStringSet(key, defaultValue))
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "getStringSet $key -> $defaultValue", "$t", true)
        defaultValue
    }
}

fun Context.putStringSet(prefsName: String, key: String, value: Set<String>): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.edit().putStringSet(key, value).commit()
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "putStringSet $key -> $value", "$t", true)
        false
    }
}

fun Context.getInt(prefsName: String, key: String, defaultValue: Int = -1): Int {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.getInt(key, defaultValue)
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "getInt $key -> $defaultValue", "$t", true)
        defaultValue
    }
}

fun Context.putInt(prefsName: String, key: String, value: Int): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.edit().putInt(key, value).commit()
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "putInt $key -> $value", "$t", true)
        false
    }
}

fun Context.getLong(prefsName: String, key: String, defaultValue: Long = -1L): Long {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.getLong(key, defaultValue)
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "getLong $key -> $defaultValue", "$t", true)
        defaultValue
    }
}

fun Context.putLong(prefsName: String, key: String, value: Long): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.edit().putLong(key, value).commit()
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "putLong $key -> $value", "$t", true)
        false
    }
}

fun Context.getFloat(prefsName: String, key: String, defaultValue: Float = -1F): Float {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.getFloat(key, defaultValue)
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "getFloat $key -> $defaultValue", "$t", true)
        defaultValue
    }
}

fun Context.putFloat(prefsName: String, key: String, value: Float): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.edit().putFloat(key, value).commit()
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "putFloat $key -> $value", "$t", true)
        false
    }
}

fun Context.getBoolean(prefsName: String, key: String, defaultValue: Boolean = false): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.getBoolean(key, defaultValue)
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "getBoolean $key -> $defaultValue", "$t", true)
        defaultValue
    }
}

fun Context.putBoolean(prefsName: String, key: String, value: Boolean): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.edit().putBoolean(key, value).commit()
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "putBoolean $key -> $value", "$t", true)
        false
    }
}

/**
 * 删除键值数据
 * @receiver Context
 * @param prefsName String
 * @param key String
 */
fun Context.removeKey(prefsName: String, key: String): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.edit().remove(key).commit()
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "removeKey $key", "$t", true)
        false
    }
}

/**
 * 删除此配置键值数据
 * @receiver Context
 * @param prefsName String?
 * @return Boolean
 */
fun Context.clearPrefs(prefsName: String): Boolean {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.edit().clear().commit()
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "clearPrefs $prefsName", "$t", true)
        false
    }
}

/**
 * 删除全部配置键值数据
 * @receiver Context
 * @param prefList Array<out String?>
 */
fun Context.clearAllPrefs(vararg prefList: String): Boolean {
    val curStatus = BooleanArray(prefList.size)
    prefList.forEachIndexed { index, name ->
        try {
            val prefs = getSharedPreferences(name, Context.MODE_WORLD_READABLE)
            curStatus[index] = prefs.edit().clear().commit()
        } catch (t: Throwable) {
            LogUtils.e("SPUtils", "clearAllPrefs $name", "$t", true)
            curStatus[index] = false
        }
    }
    return curStatus.contains(false)
}

/**
 * 获取配置键值数据
 * @receiver Context
 * @param prefsName String?
 * @return MutableMap<String, *>?
 */
fun Context.backupPrefs(prefsName: String): MutableMap<String, *> {
    return try {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        prefs.all
    } catch (t: Throwable) {
        LogUtils.e("SPUtils", "backupPrefs $prefsName", "$t", true)
        arrayMapOf<String, Any>()
    }
}

/**
 * 获取配置键值数据
 * @receiver Context
 * @param prefList Array<out String?>
 * @return ArrayMap<String, MutableMap<String, *>>?
 */
fun Context.backupAllPrefs(vararg prefList: String): ArrayMap<String, MutableMap<String, *>?> {
    val map = ArrayMap<String, MutableMap<String, *>?>()
    prefList.forEachIndexed { _, name ->
        try {
            val prefs = getSharedPreferences(name, Context.MODE_WORLD_READABLE)
            map[name] = prefs.all
        } catch (t: Throwable) {
            LogUtils.e("SPUtils", "backupAllPrefs $name", "$t", true)
            map[name] = arrayMapOf<String, Any>()
        }
    }
    return map
}