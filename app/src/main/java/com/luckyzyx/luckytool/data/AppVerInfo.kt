package com.luckyzyx.luckytool.data

import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.safeOfNull
import org.json.JSONObject
import java.io.Serializable

@Obfuscate
data class AppVerInfo(
    var appName: CharSequence,
    var packName: String,
    var versionName: String,
    var versionCode: Long,
    var versionCommit: String
) : Serializable {

    constructor() : this("", "", "", 0L, "")

    fun toAppVerInfo(jsonString: String?): AppVerInfo? {
        val jsonObject = safeOfNull { jsonString?.let { JSONObject(it) } }
        return jsonObject?.let { toAppVerInfo(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate")
    fun toAppVerInfo(jsonObject: JSONObject?): AppVerInfo? {
        if (jsonObject == null) return null
        val appName = jsonObject.optString("appName")
        val packName = jsonObject.optString("packName")
        val versionName = jsonObject.optString("versionName")
        val versionCode = jsonObject.optLong("versionCode")
        val versionCommit = jsonObject.optString("versionCommit")
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