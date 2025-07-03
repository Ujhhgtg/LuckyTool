package com.luckyzyx.luckytool.data

import com.luckyzyx.luckytool.utils.safeOfNull
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
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

    fun toAppVerInfo(jsonString: String): AppVerInfo? {
        return safeOfNull { JSONObject(jsonString) }?.let { toAppVerInfo(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toAppVerInfo(jsonObject: JSONObject): AppVerInfo {
        appName = jsonObject.optString("appName")
        packName = jsonObject.optString("packName")
        versionName = jsonObject.optString("versionName")
        versionCode = jsonObject.optLong("versionCode")
        versionCommit = jsonObject.optString("versionCommit")
        return this
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