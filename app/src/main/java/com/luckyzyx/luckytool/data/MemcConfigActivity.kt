package com.luckyzyx.luckytool.data

import com.luckyzyx.luckytool.utils.safeOfNull
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class MemcConfigActivity(
    var packName: String,
    var activity: String,
    var type: String
) : Serializable {
    constructor() : this("", "", "")

    fun toMemcConfigActivity(jsonString: String): MemcConfigActivity? {
        return safeOfNull { JSONObject(jsonString) }?.let { toMemcConfigActivity(it) }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun toMemcConfigActivity(jsonObject: JSONObject): MemcConfigActivity {
        packName = jsonObject.optString("packName")
        activity = jsonObject.optString("activity")
        type = jsonObject.optString("type")
        return this
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("packName", packName)
            put("activity", activity)
            put("type", type)
        }
    }
}