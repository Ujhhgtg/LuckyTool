package com.luckyzyx.luckytool.hook.scopes.intentresolver

import android.content.ComponentName
import android.content.Intent
import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ComponentNameClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.luckyzyx.luckytool.data.AppIntentInfo
import com.luckyzyx.luckytool.utils.IntentPrefs
import com.luckyzyx.luckytool.utils.safeOf
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HideAppShareIntentList : YukiBaseHooker() {
    override fun onHook() {
        val allIntent = ArrayList<AppIntentInfo>()
        val enabledApp = prefs(IntentPrefs).getStringSet("share_app_hide_list", ArraySet())
        enabledApp.forEachIndexed { _, packName ->
            prefs(IntentPrefs).getStringSet(packName, ArraySet()).forEachIndexed { _, js ->
                val jsonObject = safeOf(JSONObject()) { JSONObject(js) }
                allIntent.add(AppIntentInfo().toAppIntentInfo(jsonObject))
            }
        }

        //Source ResolverListController
        "com.android.intentresolver.ResolverListController".toClass().apply {
            method {
                name = "addResolveListDedupe"
                param(ListClass, IntentClass, ListClass)
            }.hook {
                after {
                    val list = args().first().cast<ArrayList<Any>>()
                    val intent = args(args.indexOfFirst { it is Intent }).cast<Intent>()
                    val action = intent?.action ?: return@after
                    if (action != Intent.ACTION_SEND && action != Intent.ACTION_SEND_MULTIPLE) return@after
                    list?.removeIf { info ->
                        val component = info.current().field { type = ComponentNameClass }
                            .cast<ComponentName>() ?: return@removeIf false
                        val packName = component.packageName
                        val activity = component.className
                        allIntent.find {
                            it.action == action && it.packName == packName && it.activity == activity
                        } != null
                    }
                }
            }
        }
    }
}