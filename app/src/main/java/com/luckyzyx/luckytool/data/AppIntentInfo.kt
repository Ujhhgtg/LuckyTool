package com.luckyzyx.luckytool.data

import android.content.pm.ResolveInfo
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class AppIntentInfo(
    var name: CharSequence,
    var packName: String,
    var action: String,
    var type: String,
    var resolveInfo: ResolveInfo,
    var activity: String? = ""
) : Serializable {

    constructor() : this("", "", "", "", ResolveInfo())

    fun toAppIntentInfo(jsonObject: JSONObject): AppIntentInfo {
        name = jsonObject.optString("name")
        packName = jsonObject.optString("packName")
        action = jsonObject.optString("action")
        type = jsonObject.optString("type")
        resolveInfo = ResolveInfo()
        activity = jsonObject.optString("activity")
        return this
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("name", name)
            put("packName", packName)
            put("action", action)
            put("type", type)
            put("activity", resolveInfo.activityInfo.name)
        }
    }
}