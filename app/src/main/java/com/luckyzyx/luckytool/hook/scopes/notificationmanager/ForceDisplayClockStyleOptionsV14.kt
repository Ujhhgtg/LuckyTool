package com.luckyzyx.luckytool.hook.scopes.notificationmanager

import android.annotation.SuppressLint
import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
@Suppress("unused", "ConstPropertyName")
object ForceDisplayClockStyleOptionsV14 : YukiBaseHooker() {
    private const val searchItemBuilder =
        "com.oplus.keyguard.settingsearch.KeyguardSettingsSearchProvider\$SearchItem\$Builder"
    private const val providerClient = "com.oplus.keyguard.common.KeyguardSettingProviderClient"
    private const val type = "TYPE_PREFRENCE_JUMP"
    private const val key = "key_keyguard_land_clock_screen"
    private const val category = "key_keyguard_category"
    private var launcherActivity = "com.android.launcher.settings.LauncherSettingsActivity"
    private var launcherAction = "com.android.launcher.action.settings.LAUNCHER_SETTINGS"
    private var launcherPackName = "com.android.launcher"

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        //Source KeyguardLauncherPageProvider
        "com.oplus.keyguard.keyguardsettings.KeyguardLauncherPageProvider".toClass().apply {
            method { name = "initKeyguardLandClockPf" }.hook {
                before {
                    val isFlavorTwoDevice = providerClient.toClass().field {
                        name = "isFlavorTwoDevice"
                    }.get().boolean()
                    if (!isFlavorTwoDevice) return@before

                    val list = args().first().cast<ArrayList<Any>>()
                    val context = method { name = "getContext";superClass() }.get(instance)
                        .invoke<Context>()
                    val clockTitle = context?.getString(
                        context.resources.getIdentifier(
                            "oplus_keyguard_land_clock_type_title", "string",
                            this@ForceDisplayClockStyleOptionsV14.packageName
                        )
                    )
                    val keyguardLandClockPf =
                        method { name = "createPerfrenceBean";superClass() }.get(instance)
                            .call(type, key, 70, clockTitle, category)
                    keyguardLandClockPf?.current()?.method { name = "setIntentPackage" }
                        ?.call("com.oplus.notificationmanager")
                    keyguardLandClockPf?.current()?.method { name = "setIntentClass" }
                        ?.call("com.oplus.keyguard.keyguardsettings.KeyguardLandClockActivity")

                    val hashMap = field { name = "preferenceHashMap" }.get(instance)
                        .cast<HashMap<String, Any>>()
                    method { name = "addPreferenceMap" }.get(instance).call(
                        hashMap, key, keyguardLandClockPf
                    )
                    keyguardLandClockPf?.let { list?.add(it) }
                    resultNull()
                }
            }
        }

        //Source KeyguardSettingsSearchProvider
//        "com.oplus.keyguard.settingsearch.KeyguardSettingsSearchProvider".toClass().apply {
//            method { name = "initSearchData";paramCount = 1 }.hook {
//                after {
//                    val isFlavorTwoDevice = providerClient.toClass().field {
//                        name = "isFlavorTwoDevice"
//                    }.get().boolean()
//                    if (!isFlavorTwoDevice) return@after
//
//                    val context = args().first().cast<Context>() ?: return@after
//                    val subTitle = context.resources.getIdentifier(
//                        "settings_search_sub_title",
//                        "string",
//                        this@ForceDisplayClockStyleOptionsV14.packageName
//                    )
//                    val clockTitle = context.resources.getIdentifier(
//                        "oplus_keyguard_land_clock_type_title",
//                        "string",
//                        this@ForceDisplayClockStyleOptionsV14.packageName
//                    )
//                    val settingsWallpaper = context.resources.getIdentifier(
//                        "settings_wallpaper_ic",
//                        "drawable",
//                        this@ForceDisplayClockStyleOptionsV14.packageName
//                    )
//
//                    val builder = searchItemBuilder.toClass().field { name = "INSTANCE" }.get()
//                        .any() ?: return@after
//                    val build = builder.current().method {
//                        name = "build";param { it[4] == IntType && it[7] == IntArrayType }
//                    }.call(
//                        context, 2, settingsWallpaper, key, clockTitle, -1, -1,
//                        intArrayOf(subTitle, clockTitle), launcherActivity, launcherAction,
//                        launcherPackName, launcherActivity, -1
//                    )
//                    method { name = "autoAddItems" }.get(instance).call(build)
//                }
//            }
//        }
    }
}