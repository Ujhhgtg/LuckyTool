package com.luckyzyx.luckytool.hook.scopes.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageItemInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ComponentInfoClass
import com.highcapable.yukihookapi.hook.type.android.ComponentNameClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.DrawableClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class EnableGoogleAutoFill(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        if (getOSVersionCode >= 30) loadHooker(GoogleAutoFill)
        else loadHooker(GoogleAutoFillV13(dexKitBridge))
    }

    @Obfuscate
    object GoogleAutoFill : YukiBaseHooker() {
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

    @Obfuscate
    class GoogleAutoFillV13(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            //Source DefaultAppInfo
            val defaultAppInfoClazz = dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(IntType)
                        addForType(StringClass)
                        addForType(ContextClass)
                        addForType(ComponentNameClass)
                        addForType(PackageManager::class.java)
                        addForType(PackageItemInfo::class.java)
                    }
                    methods {
                        add { paramCount(0);returnType(StringClass) }
                        add { paramCount(0);returnType(DrawableClass) }
                        add { paramCount(0);returnType(CharSequenceClass) }
                        add { paramCount(0);returnType(ComponentInfoClass) }
                    }
                }
            }.let {
                it.checkDataList("GoogleAutoFillV13")
                it.single().name
            }

            //Source OplusDefaultAutofillPicker
            "com.oplus.settings.feature.othersettings.input.OplusDefaultAutofillPicker".toClass()
                .apply {
                    method { emptyParam();returnType = ListClass }.hook {
                        before {
                            val list = ArrayList<Any>()
                            val context = method { name = "getContext";superClass() }.get(instance)
                                .invoke<Context>() ?: return@before
                            val packageManager = field {
                                type = PackageManager::class.java;superClass()
                            }.get(instance).cast<PackageManager>() ?: return@before
                            val intent = field {
                                type = IntentClass;superClass()
                            }.get(instance).cast<Intent>() ?: return@before
                            val users = UserHandleClass.method { name = "myUserId" }.get()
                                .call()
                            val queryIntentServicesAsUser = packageManager.current().method {
                                name = "queryIntentServicesAsUser"
                                param(IntentClass, IntType, IntType)
                            }.invoke<List<ResolveInfo>>(intent, 128, users) ?: return@before
                            queryIntentServicesAsUser.forEachIndexed { _, resolveInfo ->
                                val serviceInfo = resolveInfo.serviceInfo
                                val permission = serviceInfo.permission
                                if (permission == "android.permission.BIND_AUTOFILL_SERVICE") {
                                    val defaultAppInfo = defaultAppInfoClazz.toClass().buildOf(
                                        context, packageManager, users, ComponentName(
                                            serviceInfo.packageName, serviceInfo.name
                                        )
                                    ) {
                                        param(
                                            ContextClass, PackageManager::class.java, IntType,
                                            ComponentNameClass
                                        )
                                    }
                                    defaultAppInfo?.let { list.add(it) }
                                }
                                if (permission == "android.permission.BIND_AUTOFILL") {
                                    val defaultAppInfo = defaultAppInfoClazz.toClass().buildOf(
                                        context, packageManager, users, ComponentName(
                                            serviceInfo.packageName, serviceInfo.name
                                        )
                                    ) {
                                        param(
                                            ContextClass, PackageManager::class.java, IntType,
                                            ComponentNameClass
                                        )
                                    }
                                    defaultAppInfo?.let { list.add(it) }
                                }
                            }
                            if (list.isNotEmpty()) result = list
                        }
                    }
                }
        }
    }
}