package com.luckyzyx.luckytool.data

import com.luckyzyx.commonutils.safeOfNull
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Obfuscate
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