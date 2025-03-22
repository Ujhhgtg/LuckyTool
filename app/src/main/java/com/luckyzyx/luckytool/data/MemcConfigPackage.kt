package com.luckyzyx.luckytool.data

import com.luckyzyx.luckytool.utils.safeOfNull
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class MemcConfigPackage(
    var packName: String,
    var rate: String,
    var type: String
) : Serializable {
    constructor() : this("", "", "")

    fun toMemcConfigPackage(jsonString: String): MemcConfigPackage? {
        return safeOfNull { JSONObject(jsonString) }?.let { toMemcConfigPackage(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toMemcConfigPackage(jsonObject: JSONObject): MemcConfigPackage {
        packName = jsonObject.optString("packName")
        rate = jsonObject.optString("rate")
        type = jsonObject.optString("type")
        return this
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("packName", packName)
            put("rate", rate)
            put("type", type)
        }
    }
}