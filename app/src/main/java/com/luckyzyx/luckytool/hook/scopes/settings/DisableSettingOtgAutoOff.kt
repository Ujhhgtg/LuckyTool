package com.luckyzyx.luckytool.hook.scopes.settings

import android.content.Context
import android.os.customize.OplusCustomizeRestrictionManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object DisableSettingOtgAutoOff : YukiBaseHooker() {
    override fun onHook() {
        //Source OtgConnectionOpenedPreferenceController
        "com.oplus.settings.feature.othersettings.controller.OtgConnectionOpenedPreferenceController".toClass()
            .apply {
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