package com.luckyzyx.luckytool.data

import com.luckyzyx.commonutils.safeOfNull
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Obfuscate
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