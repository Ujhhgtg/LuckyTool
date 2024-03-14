package com.luckyzyx.luckytool.hook.scopes.settings

import android.content.Context
import android.content.pm.PackageItemInfo
import android.content.pm.PackageManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass

object EnableGoogleAutoFill : YukiBaseHooker() {
    override fun onHook() {
        //Source DefaultAppInfo
        val defaultAppInfoClazz = "com.android.settingslib.applications.DefaultAppInfo"

        //Source OplusDefaultAutofillPicker
        "com.oplus.settings.feature.othersettings.input.OplusDefaultAutofillPicker".toClass()
            .apply {
                method { name = "getCandidates" }.hook {
                    before {
                        val list = ArrayList<Any>()
                        val context = method { name = "getContext";superClass() }.get(instance)
                            .invoke<Context>() ?: return@before
                        val pm = field {
                            type = PackageManager::class.java;superClass()
                        }.get(instance).cast<PackageManager>() ?: return@before
                        val allProviders = method {
                            name = "getAllProviders";superClass()
                        }.get(instance).list<Any>()
                        val users = method {
                            name = "getUser";superClass()
                        }.get(instance).int()
                        allProviders.forEachIndexed { _, info ->
                            val settingsSubtitle =
                                info.current().method { name = "getSettingsSubtitle" }.call()
                            val brandingService =
                                info.current().method { name = "getBrandingService" }.call()
                            val defaultAppInfo = defaultAppInfoClazz.toClass().buildOf(
                                context, pm, users, brandingService, settingsSubtitle, true
                            ) {
                                param(
                                    ContextClass, PackageManager::class.java, IntType,
                                    PackageItemInfo::class.java, StringClass, BooleanType
                                )
                            }
                            defaultAppInfo?.let { list.add(it) }
                        }
                        if (list.isNotEmpty()) result = list
                    }
                }
//                method { name = "newAddServicePreferenceOrNull" }.hook {
//                    before {
//                        val context = method { name = "getPrefContext";superClass() }.get(instance)
//                            .invoke<Context>() ?: return@before
//                        val preference = method {
//                            name = "newAddServicePreferenceOrNull";superClass()
//                        }.get(instance).original().call()
//                        YLog.debug("newAddServicePreferenceOrNull -> ${preference.toString()}")
//
//                        if (preference != null) {
//                            val title = context.resources.getIdentifier(
//                                "add_autofill_services", "string", this@HookSettings.packageName
//                            )
//                            if (title == 0) return@before
//                            preference.current().method { name = "setTitle";param(IntType) }.call(title)
//                            preference.current().method { name = "setOrder";param(IntType) }
//                                .call(2147483645)
//                            preference.current().method { name = "setPersistent";param(BooleanType) }
//                                .call(false)
//                            preference.current().method { name = "setKey";param(StringClass) }
//                                .call("add_autofill_services")
//
//                            YLog.debug("preference -> ${preference.toString()}")
//
//                            result = preference
//                        }
//                    }
//                }
            }
    }
}