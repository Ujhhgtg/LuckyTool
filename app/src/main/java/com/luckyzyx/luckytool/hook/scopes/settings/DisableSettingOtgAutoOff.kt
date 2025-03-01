package com.luckyzyx.luckytool.hook.scopes.settings

import android.content.Context
import android.os.customize.OplusCustomizeRestrictionManager
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object DisableSettingOtgAutoOff : YukiBaseHooker() {
    override fun onHook() {
        //Source OtgConnectionOpenedPreferenceController
        VariousClass(
            "com.oplus.settings.feature.othersettings.controller.OtgConnectionOpenedPreferenceController",  //C14
            "com.oplus.settings.feature.spfunction.OtgConnectionOpenedPreferenceController"  //C14.1 C15
        ).toClass().apply {
            method { name = "isPreferenceSupport" }.hook {
                before {
                    val context = args().first().cast<Context>() ?: return@before
                    val isUSBOtgDisabled =
                        OplusCustomizeRestrictionManager.getInstance(context).isUSBOtgDisabled
                    result = !isUSBOtgDisabled
                }
            }
        }
    }
}