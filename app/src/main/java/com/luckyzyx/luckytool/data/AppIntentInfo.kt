package com.luckyzyx.luckytool.data

import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import com.luckyzyx.luckytool.enums.IntentType
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
import java.io.Serializable

@Obfuscate
data class AppIntentInfo(
    var name: CharSequence,
    var packName: String,
    var action: String,
    var type: IntentType,
    var resolveInfo: ResolveInfo,
    var activity: String = ""
) : Serializable {

    constructor() : this("", "", "", IntentType.UNKNOWN, ResolveInfo())

    fun toAppIntentInfo(jsonObject: JSONObject): AppIntentInfo {
        name = jsonObject.optString("name")
        packName = jsonObject.optString("packName")
        action = jsonObject.optString("action")
        type = IntentType.fromString(jsonObject.optString("type"))
        activity = jsonObject.optString("activity")
        resolveInfo = ResolveInfo().apply {
            activityInfo = ActivityInfo().apply {
                name = activity
            }
        }
        return this
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("name", name)
            put("packName", packName)
            put("action", action)
            put("type", type.toString())
            put("activity", resolveInfo.activityInfo?.name ?: activity)
        }
    }
}