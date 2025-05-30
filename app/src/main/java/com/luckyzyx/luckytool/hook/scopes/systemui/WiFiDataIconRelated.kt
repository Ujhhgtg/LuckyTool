package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.hook.utils.FlowUtils
import com.luckyzyx.luckytool.hook.utils.sysui.WifiUtils
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

        val CommonSettingsValueProxy =
            "com.oplusos.systemui.common.settingsvalue.CommonSettingsValueProxy"

        @SuppressLint("MissingPermission", "DiscouragedApi")
        override fun onHook() {
            val removeInout = prefs(ModulePrefs).getBoolean("remove_wifi_data_inout", false)
            val wifiStandard = prefs(ModulePrefs).getBoolean("force_display_wifi_standard", false)

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
                                FlowUtils(appClassLoader).getValue<Int>(result!!) ?: -1
                            if (originalValue <= 0) return@after

                            result = FlowUtils(appClassLoader).let {
                                val mutableStateFlow = it.MutableStateFlow(-1)
                                    ?: return@after
                                it.asStateFlow(mutableStateFlow) ?: return@after
                            }
                        }
                    }
                    method {
                        name = "getWifiLeftResId"
                        returnType = "kotlinx.coroutines.flow.StateFlow"
                    }.hook {
                        after {
                            if (!wifiStandard) return@after

                            val context = field { type = ContextClass }.get(instance)
                                .cast<Context>() ?: return@after
                            val manager = context.getSystemService(ConnectivityManager::class.java)
                            val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
                                ?: return@after

                            val valiNet =
                                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                            val isDual = WifiUtils(appClassLoader).isDualWifiConnected(context)
                            val isAp = WifiUtils(appClassLoader).isPassPointAp(context)
                            if ((valiNet && isDual) || isAp) return@after

                            val technicalEnable = CommonSettingsValueProxy.toClass().method {
                                name = "getWifiTechnicalStandardState";param(ContextClass)
                            }.get().int(context)
                            if (technicalEnable != 1) return@after

                            val wifiInfo = capabilities.transportInfo ?: return@after
                            if (wifiInfo !is WifiInfo) return@after
                            context.injectModuleAppResources()
                            val drawable = when (wifiInfo.wifiStandard) {
//                                ScanResult.WIFI_STANDARD_UNKNOWN -> 0
//                                ScanResult.WIFI_STANDARD_LEGACY -> 0
                                ScanResult.WIFI_STANDARD_11N -> R.drawable.stat_signal_wifi_4

                                ScanResult.WIFI_STANDARD_11AC -> R.drawable.stat_signal_wifi_5

                                ScanResult.WIFI_STANDARD_11AX -> R.drawable.stat_signal_wifi_6
//                                ScanResult.WIFI_STANDARD_11AD -> 0
                                ScanResult.WIFI_STANDARD_11BE -> R.drawable.stat_signal_wifi_7

                                else -> return@after
                            }
                            result = FlowUtils(appClassLoader).let {
                                val mutableStateFlow = it.MutableStateFlow(drawable)
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