package com.luckyzyx.luckytool.data

import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.safeOfNull
import org.json.JSONObject
import java.io.Serializable

@Obfuscate
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