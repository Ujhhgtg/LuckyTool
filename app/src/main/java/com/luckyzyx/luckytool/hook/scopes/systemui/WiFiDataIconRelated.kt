package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object WiFiDataIconRelated : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        when (osCode) {
            in 34..Int.MAX_VALUE -> loadHooker(WiFiDataIcon)
            else -> loadHooker(WiFiDataIconV14)
        }
    }

    @Obfuscate
    object WiFiDataIcon : YukiBaseHooker() {
        override fun onHook() {
            val removeInout = prefs(ModulePrefs).getBoolean("remove_wifi_data_inout", false)

            //Source OplusWifiSignalExImpl
            "com.oplus.systemui.statusbar.pipeline.OplusWifiSignalExImpl".toClass().apply {
                val hasActivityIcon = hasMethod { name = "bindEx\$updateActivityIcon" }
                if (hasActivityIcon) method { name = "bindEx\$updateActivityIcon" }.hook {
                    before {
                        if (removeInout) args().last().set(0)
                    }
                }
            }
        }
    }

    @Obfuscate
    object WiFiDataIconV14 : YukiBaseHooker() {
        override fun onHook() {
            val removeInout = prefs(ModulePrefs).getBoolean("remove_wifi_data_inout", false)

            //Source OplusStatusBarWifiView
            VariousClass(
                "com.oplusos.systemui.statusbar.OplusStatusBarWifiView",
                "com.oplus.systemui.statusbar.phone.signal.OplusStatusBarWifiViewExImpl"
            ).toClass().apply {
                method { name = "initViewState" }.hook {
                    after {
                        if (!removeInout) return@after
                        field { name = "mWifiActivity" }.get(instance).cast<View>()?.isVisible =
                            false
                    }
                }
                method { name = "updateState" }.hook {
                    after {
                        if (!removeInout) return@after
                        field { name = "mWifiActivity" }.get(instance).cast<View>()?.isVisible =
                            false
                    }
                }
            }
        }
    }
}