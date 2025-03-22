package com.luckyzyx.luckytool.data

import com.luckyzyx.luckytool.utils.safeOfNull
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class DarkModeInfo(
    var packName: String,
    var curType: Int = 0,
) : Serializable {
    constructor() : this("")

    fun toDarkModeInfo(jsonString: String): DarkModeInfo? {
        return safeOfNull { JSONObject(jsonString) }?.let { toDarkModeInfo(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toDarkModeInfo(jsonObject: JSONObject): DarkModeInfo {
        packName = jsonObject.optString("packName")
        curType = jsonObject.optInt("curType")
        return this
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("packName", packName)
            put("curType", curType)
        }
    }
}