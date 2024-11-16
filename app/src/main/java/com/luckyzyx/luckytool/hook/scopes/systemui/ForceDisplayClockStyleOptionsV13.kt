package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.content.Context
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object ForceDisplayClockStyleOptionsV13 : YukiBaseHooker() {
    private const val flavorTwoFeatureOption =
        "com.oplusos.systemui.common.feature.FlavorTwoFeatureOption"
    private const val type = "TYPE_PREFRENCE_JUMP"
    private const val key = "key_keyguard_land_clock_screen"
    private const val category = "key_keyguard_category"

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        //Source KeyguardLauncherPageProvider
        VariousClass(
            "com.oplusos.systemui.keyguard.keyguardsetting.KeyguardLauncherPageProvider", //C13.0
            "com.oplus.systemui.keyguard.keyguardsetting.KeyguardLauncherPageProvider" //C13.1
        ).toClass().apply {
            method { name = "initKeyguardLandClockPf" }.hook {
                before {
                    val isFlavorTwoDevice = flavorTwoFeatureOption.toClass().method {
                        name = "isFlavorTwoDevice"
                    }.get().boolean()
                    if (!isFlavorTwoDevice) return@before

                    val list = args().first().cast<ArrayList<Any>>()
                    val context = method { name = "getContext";superClass() }.get(instance)
                        .invoke<Context>()
                    val clockTitle = context?.getString(
                        context.resources.getIdentifier(
                            "oplus_keyguard_land_clock_type_title", "string",
                            this@ForceDisplayClockStyleOptionsV13.packageName
                        )
                    )
                    val keyguardLandClockPf =
                        method { name = "createPerfrenceBean";superClass() }.get(instance)
                            .call(type, key, 70, clockTitle, category)
                    keyguardLandClockPf?.current()?.method { name = "setIntentPackage" }
                        ?.call("com.android.systemui")
                    keyguardLandClockPf?.current()?.method { name = "setIntentClass" }
                        ?.call("com.oplus.systemui.keyguard.keyguardsetting.KeyguardLandClockActivity")

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
    }
}