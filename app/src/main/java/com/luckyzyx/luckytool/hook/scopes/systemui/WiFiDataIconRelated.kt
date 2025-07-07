package com.luckyzyx.luckytool.hook.scopes.systemui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.hook.utils.FlowUtils
import com.luckyzyx.luckytool.hook.utils.sysui.AbsSettingsValueProxyUtils
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

        private val hasRegisterCallback = false
        private var wifiInfo: WifiInfo? = null

        @SuppressLint("MissingPermission", "DiscouragedApi")
        override fun onHook() {
            val removeInout = prefs(ModulePrefs).getBoolean("remove_wifi_data_inout", false)
            val wifiStandard = prefs(ModulePrefs).getBoolean("force_display_wifi_standard", false)

            val mNetworkRequest = NetworkRequest.Builder().apply {
                addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }.build()

            //Source ModernStatusBarWifiView
            "com.android.systemui.statusbar.pipeline.wifi.ui.view.ModernStatusBarWifiView".toClass()
                .resolve().apply {
                    firstConstructor().hook {
                        after {
                            if (!wifiStandard) return@after
                            val view = instance<View>()
                            val context = view.context
                            val manager = context.getSystemService(ConnectivityManager::class.java)
                            if (!hasRegisterCallback) {
                                val handlerThread = HandlerThread("SysUiNetwork", 10)
                                handlerThread.start()
                                handlerThread.looper.asResolver().firstMethod {
                                    name = "setSlowLogThresholdMs"
                                    parameters(
                                        Long::class, Long::class
                                    )
                                }.invoke(1000L, 1000L)
                                handlerThread.looper.asResolver().firstMethod {
                                    name = "setTraceTag"
                                    parameters(Long::class)
                                }.invoke(4096L)
                                val looper = handlerThread.looper
                                val handler = Handler(looper)
                                val callback = object : ConnectivityManager.NetworkCallback() {
                                    override fun onCapabilitiesChanged(
                                        network: Network,
                                        networkCapabilities: NetworkCapabilities
                                    ) {
                                        val info = networkCapabilities.transportInfo?.let {
                                            it as? WifiInfo
                                        } ?: return
                                        val isPrimary = info.asResolver().firstMethod {
                                            name = "isPrimary";emptyParameters()
                                        }.invoke<Boolean>() ?: false
                                        if (!isPrimary) return
                                        wifiInfo = info

                                        val drawable = getSignalDrawable(
                                            appClassLoader, context, wifiInfo!!.wifiStandard
                                        )
                                        if (drawable < 0) return
                                        view.findViewById<ImageView>(
                                            context.resources.getIdentifier(
                                                "wifi_left", "id", this@WiFiDataIcon.packageName
                                            )
                                        )?.setImageResource(drawable)
                                    }
                                }
                                manager.registerNetworkCallback(mNetworkRequest, callback, handler)
                            }
                        }
                    }
                }

            //Source OplusWifiViewModel
            "com.oplus.systemui.statusbar.pipeline.wifi.ui.viewmodel.OplusWifiViewModel".toClass()
                .resolve().apply {
                    firstMethod {
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
                    firstMethod {
                        name = "getWifiLeftResId"
                        returnType = "kotlinx.coroutines.flow.StateFlow"
                    }.hook {
                        after {
                            if (!wifiStandard) return@after

                            val context = firstField { type = Context::class }.of(instance)
                                .get<Context>() ?: return@after
                            if (wifiInfo == null) return@after
                            val drawable =
                                getSignalDrawable(appClassLoader, context, wifiInfo!!.wifiStandard)
                            if (drawable < 0) return@after
                            result = FlowUtils(appClassLoader).let {
                                val mutableStateFlow = it.MutableStateFlow(drawable)
                                    ?: return@after
                                it.asStateFlow(mutableStateFlow) ?: return@after
                            }
                        }
                    }
                }
        }

        fun getSignalDrawable(classLoader: ClassLoader?, context: Context, standard: Int): Int {
            context.injectModuleAppResources()
            val isDual = WifiUtils(classLoader).isDualWifiConnected(context)
            val isAp = WifiUtils(classLoader).isPassPointAp(context)
            if (isDual || isAp) return -1

            val technicalEnable = AbsSettingsValueProxyUtils(appClassLoader)
                .getGlobalIntValue(context, "wifi_use_technical_standard_icons_switch_on", 0)
            if (technicalEnable != 1) return -1

            return when (standard) {
//                ScanResult.WIFI_STANDARD_UNKNOWN -> 0
//                ScanResult.WIFI_STANDARD_LEGACY -> 0
                ScanResult.WIFI_STANDARD_11N -> R.drawable.stat_signal_wifi_4

                ScanResult.WIFI_STANDARD_11AC -> R.drawable.stat_signal_wifi_5

                ScanResult.WIFI_STANDARD_11AX -> R.drawable.stat_signal_wifi_6
//                ScanResult.WIFI_STANDARD_11AD -> 0
                ScanResult.WIFI_STANDARD_11BE -> R.drawable.stat_signal_wifi_7

                else -> -1
            }
        }

    }

    @Obfuscate
    object WiFiDataIconV14 : YukiBaseHooker() {
        override fun onHook() {
            val removeInout = prefs(ModulePrefs).getBoolean("remove_wifi_data_inout", false)

            //Source OplusStatusBarWifiView
            (VariousClass(
                "com.oplusos.systemui.statusbar.OplusStatusBarWifiView",
                "com.oplus.systemui.statusbar.phone.signal.OplusStatusBarWifiViewExImpl"
            ).toClass() as Class<Any>).resolve().apply {
                firstMethod { name = "initViewState" }.hook {
                    after {
                        if (!removeInout) return@after
                        firstField { name = "mWifiActivity" }.of(instance).get<View>()?.isVisible =
                            false
                    }
                }
                firstMethod { name = "updateState" }.hook {
                    after {
                        if (!removeInout) return@after
                        firstField { name = "mWifiActivity" }.of(instance).get<View>()?.isVisible =
                            false
                    }
                }
            }
        }
    }
}