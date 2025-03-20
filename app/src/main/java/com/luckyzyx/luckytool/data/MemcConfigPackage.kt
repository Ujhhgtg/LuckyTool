package com.luckyzyx.luckytool.data

import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.safeOfNull
import org.json.JSONObject
import java.io.Serializable

@Obfuscate
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