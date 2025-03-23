package com.luckyzyx.luckytool.hook.scopes.android

import android.content.Intent
import android.content.pm.ParceledListSlice
import android.content.pm.ResolveInfo
import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.data.AppIntentInfo
import com.luckyzyx.luckytool.enums.IntentType
import com.luckyzyx.luckytool.utils.IntentPrefs
import com.luckyzyx.luckytool.utils.IntentUtils.Companion.getFilterType
import com.luckyzyx.luckytool.utils.safeOf
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class HookIPackageManager : YukiBaseHooker() {

    val allIntent = ArrayList<AppIntentInfo>()
    var isEnable = prefs(IntentPrefs).getBoolean("custom_config_app_intent_list", false)
    val enabledApps = prefs(IntentPrefs).getStringSet("enable_app_hide_list", ArraySet())

    val types = arrayOf(
        IntentType.SINGLE_SHARE, IntentType.MULTI_SHARE, IntentType.PROCESS_TEXT,
        IntentType.CONTENT, IntentType.FILE,
        IntentType.HTTP_LINK, IntentType.HTTPS_LINK
    )

    override fun onHook() {
        dataChannel.wait<Boolean>("custom_config_app_intent_list") { isEnable = it }

        initData()
        loadHooker(HookQueryIntentActivitie())
    }

    private fun initData() {
        allIntent.clear()
        enabledApps.forEachIndexed { _, packName ->
            prefs(IntentPrefs).getStringSet(packName, ArraySet()).forEachIndexed { _, js ->
                val jsonObject = safeOf(JSONObject()) { JSONObject(js) }
                allIntent.add(AppIntentInfo().toAppIntentInfo(jsonObject))
            }
        }
    }

    @Obfuscate
    inner class HookQueryIntentActivitie : YukiBaseHooker() {
        override fun onHook() {
            //Source IPackageManagerBase
            "com.android.server.pm.IPackageManagerBase".toClass().apply {
                method {
                    name = "queryIntentActivities"
                    param(IntentClass, StringClass, LongType, IntType)
                }.hook {
                    after {
                        if (!isEnable) return@after
                        val intent = args().first().cast<Intent>() ?: return@after
                        val action = intent.action ?: return@after

                        val isOrigin = intent.getBooleanExtra("result_origin_data", false)
                        if (isOrigin) return@after

                        val res = result<ParceledListSlice<ResolveInfo>>() ?: return@after
                        val list = res.list ?: return@after
                        types.forEachIndexed { _, intentType ->
                            val filte = getFilterType(intentType)
                            val intents = allIntent.filter(filte)
                            list.removeIf {
                                val packName = it.activityInfo.packageName
                                val activity = it.activityInfo.name
                                intents.find { info ->
                                    info.action == action && info.packName == packName && info.activity == activity
                                } != null
                            }
                        }
                    }
                }
            }
        }
    }
}