package com.luckyzyx.luckytool.utils

import android.graphics.drawable.Drawable
import org.json.JSONObject
import java.io.Serializable

data class AppInfo(
    var appIcon: Drawable,
    var appName: CharSequence,
    var packName: String,
) : Serializable

data class AppVerInfo(
    var appName: CharSequence?,
    var packName: String,
    var versionName: String?,
    var versionCode: Long?,
    var versionCommit: String?
) : Serializable {

    constructor() : this(null, "", null, null, null)

    fun toAppVerInfo(jsonString: String?): AppVerInfo? {
        val jsonObject = safeOfNull { jsonString?.let { JSONObject(it) } }
        return jsonObject?.let { toAppVerInfo(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate")
    fun toAppVerInfo(jsonObject: JSONObject?): AppVerInfo? {
        if (jsonObject == null) return null
        val appName: CharSequence? = jsonObject.optString("appName")
        val packName: String = jsonObject.optString("packName")
        val versionName: String? = jsonObject.optString("versionName")
        val versionCode: Long = jsonObject.optLong("versionCode")
        val versionCommit: String? = jsonObject.optString("versionCommit")
        return AppVerInfo(appName, packName, versionName, versionCode, versionCommit)
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("appName", appName)
            put("packName", packName)
            put("versionName", versionName)
            put("versionCode", versionCode)
            put("versionCommit", versionCommit)
        }
    }
}

data class DarkModeInfo(
    var packName: String,
    var curType: Int = 0,
) : Serializable {
    constructor() : this("")

    fun toDarkModeInfo(jsonString: String?): DarkModeInfo? {
        val jsonObject = safeOfNull { jsonString?.let { JSONObject(it) } }
        return jsonObject?.let { toDarkModeInfo(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toDarkModeInfo(jsonObject: JSONObject?): DarkModeInfo? {
        if (jsonObject == null) return null
        val packName: String = jsonObject.optString("packName")
        val curType: Int = jsonObject.optInt("curType")
        return DarkModeInfo(packName, curType)
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("packName", packName)
            put("curType", curType)
        }
    }
}

data class DisplayMode(
    val id: Int,
    val width: Int? = null,
    val height: Int? = null,
    val xDpi: Float? = null,
    val yDpi: Float? = null,
    val refreshRate: Float? = null,
    val appVsyncOffsetNanos: Long? = null,
    val presentationDeadlineNanos: Long? = null,
    val group: Int? = null,
) : Serializable

data class DonateInfo(
    val name: String,
    val time: String,
    val channel: String,
    val money: Double,
    val order: String,
    val unit: String = "RMB",
) : Serializable

data class MemcConfigPackage(
    val packName: String,
    val rate: String,
    val type: String
) : Serializable {
    constructor() : this("", "", "")

    fun toMemcConfigPackage(jsonString: String?): MemcConfigPackage? {
        val jsonObject = safeOfNull { jsonString?.let { JSONObject(it) } }
        return jsonObject?.let { toMemcConfigPackage(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toMemcConfigPackage(jsonObject: JSONObject?): MemcConfigPackage? {
        if (jsonObject == null) return null
        val packName: String = jsonObject.optString("packName")
        val rate: String = jsonObject.optString("rate")
        val type: String = jsonObject.optString("type")
        return MemcConfigPackage(packName, rate, type)
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("packName", packName)
            put("rate", rate)
            put("type", type)
        }
    }
}

data class MemcConfigActivity(
    val packName: String,
    val activity: String,
    val type: String
) : Serializable {
    constructor() : this("", "", "")

    fun toMemcConfigActivity(jsonString: String?): MemcConfigActivity? {
        val jsonObject = safeOfNull { jsonString?.let { JSONObject(it) } }
        return jsonObject?.let { toMemcConfigActivity(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toMemcConfigActivity(jsonObject: JSONObject?): MemcConfigActivity? {
        if (jsonObject == null) return null
        val packName: String = jsonObject.optString("packName")
        val activity: String = jsonObject.optString("activity")
        val type: String = jsonObject.optString("type")
        return MemcConfigActivity(packName, activity, type)
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("packName", packName)
            put("activity", activity)
            put("type", type)
        }
    }
}