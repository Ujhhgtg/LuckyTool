@file:Suppress(
    "unused", "DEPRECATION", "WorldReadableFiles", "ApplySharedPref",
    "", "UseKtx"
)

package com.luckyzyx.luckytool.utils

import android.content.Context
import android.util.ArrayMap
import androidx.collection.ArraySet

const val ModulePrefs: String = "ModulePrefs"
const val IntentPrefs: String = "IntentPrefs"
const val SettingsPrefs: String = "SettingsPrefs"
const val OtherPrefs: String = "OtherPrefs"

fun Context.putString(prefsName: String, key: String, value: String?): Boolean {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    val editor = prefs.edit()
    editor.putString(key, value)
    return editor.commit()
}

fun Context.putStringSet(prefsName: String, key: String, value: Set<String?>?): Boolean {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    val editor = prefs.edit()
    editor.putStringSet(key, value)
    return editor.commit()
}

fun Context.getString(prefsName: String, key: String): String {
    return getString(prefsName, key, "")
}

fun Context.getStringSet(prefsName: String, key: String): Set<String> {
    return getStringSet(prefsName, key, ArraySet())
}

fun Context.getString(prefsName: String, key: String, defaultValue: String) =
    safeOf(defaultValue) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        return prefs.getString(key, defaultValue)!!
    }

fun Context.getStringSet(prefsName: String, key: String, defaultValue: Set<String>) =
    safeOf(defaultValue) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        return prefs.getStringSet(key, defaultValue)!!
    }

fun Context.putInt(prefsName: String, key: String, value: Int): Boolean {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    val editor = prefs.edit()
    editor.putInt(key, value)
    return editor.commit()
}

fun Context.getInt(prefsName: String, key: String): Int {
    return getInt(prefsName, key, -1)
}

fun Context.getInt(prefsName: String, key: String, defaultValue: Int) =
    safeOf(defaultValue) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        return prefs.getInt(key, defaultValue)
    }

fun Context.putLong(prefsName: String, key: String, value: Long): Boolean {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    val editor = prefs.edit()
    editor.putLong(key, value)
    return editor.commit()
}

fun Context.getLong(prefsName: String, key: String): Long {
    return getLong(prefsName, key, -1)
}

fun Context.getLong(prefsName: String, key: String, defaultValue: Long) =
    safeOf(defaultValue) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        return prefs.getLong(key, defaultValue)
    }

fun Context.putFloat(prefsName: String, key: String, value: Float): Boolean {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    val editor = prefs.edit()
    editor.putFloat(key, value)
    return editor.commit()
}

fun Context.getFloat(prefsName: String, key: String): Float {
    return getFloat(prefsName, key, -1F)
}

fun Context.getFloat(prefsName: String, key: String, defaultValue: Float) =
    safeOf(defaultValue) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        return prefs.getFloat(key, defaultValue)
    }

fun Context.putBoolean(prefsName: String, key: String, value: Boolean): Boolean {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    val editor = prefs.edit()
    editor.putBoolean(key, value)
    return editor.commit()
}

fun Context.getBoolean(prefsName: String, key: String): Boolean {
    return getBoolean(prefsName, key, false)
}

fun Context.getBoolean(prefsName: String, key: String, defaultValue: Boolean) =
    safeOf(defaultValue) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
        return prefs.getBoolean(key, defaultValue)
    }

/**
 * 删除键值数据
 * @receiver Context
 * @param prefsName String
 * @param key String
 */
fun Context.removeKey(prefsName: String, key: String): Boolean {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    return prefs.edit().remove(key).commit()
}

/**
 * 删除此配置键值数据
 * @receiver Context
 * @param prefsName String?
 * @return Boolean
 */
fun Context.clearPrefs(prefsName: String) = runInSafe {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    prefs.edit().clear().commit()
}

/**
 * 删除全部配置键值数据
 * @receiver Context
 * @param prefList Array<out String?>
 */
fun Context.clearAllPrefs(vararg prefList: String) = runInSafe {
    prefList.forEach {
        val prefs = getSharedPreferences(it, Context.MODE_WORLD_READABLE)
        prefs.edit().clear().commit()
    }
}

/**
 * 获取配置键值数据
 * @receiver Context
 * @param prefsName String?
 * @return MutableMap<String, *>?
 */
fun Context.backupPrefs(prefsName: String): MutableMap<String, *>? = safeOfNull {
    val prefs = getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    prefs.all
}

/**
 * 获取配置键值数据
 * @receiver Context
 * @param prefList Array<out String?>
 * @return ArrayMap<String, MutableMap<String, *>>?
 */
fun Context.backupAllPrefs(vararg prefList: String) = safeOfNull {
    ArrayMap<String, MutableMap<String, *>?>().apply {
        prefList.forEach {
            val prefs = getSharedPreferences(it, Context.MODE_WORLD_READABLE)
            this[it] = prefs.all
        }
    }
}