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

    override fun toString(): String {
        return toJSONObject().toString()
    }
}

@Suppress("ArrayInDataClass")
data class DInfo(
    val name: String,
    val details: Array<DCInfo>
) : Serializable

data class DCInfo(
    val time: String,
    val channel: String,
    val money: Double,
    val order: String,
    val unit: String = "RMB"
) : Serializable

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