package com.luckyzyx.luckytool.hook.scopes.systemui

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.hook.utils.FlowUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class WiFiDataIconRelated(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        when (osCode) {
            in 34..Int.MAX_VALUE -> loadHooker(WiFiDataIcon(dexKitBridge))
            else -> loadHooker(WiFiDataIconV14)
        }
    }

    @Obfuscate
    class WiFiDataIcon(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        override fun onHook() {
            val removeInout = prefs(ModulePrefs).getBoolean("remove_wifi_data_inout", false)

            //Source OplusWifiViewModel
            "com.oplus.systemui.statusbar.pipeline.wifi.ui.viewmodel.OplusWifiViewModel".toClass()
                .apply {
                    method {
                        name = "getWifiActivityResId"
                        returnType = "kotlinx.coroutines.flow.StateFlow"
                    }.hook {
                        after {
                            if (!removeInout) return@after
                            if (result == null) return@after

                            val originalValue =
                                FlowUtils(appClassLoader).getValue(result!!) as Int
                            if (originalValue <= 0) return@after

                            result = FlowUtils(appClassLoader).let {
                                val mutableStateFlow = it.MutableStateFlow(-1)
                                    ?: return@after
                                it.asStateFlow(mutableStateFlow) ?: return@after
                            }
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