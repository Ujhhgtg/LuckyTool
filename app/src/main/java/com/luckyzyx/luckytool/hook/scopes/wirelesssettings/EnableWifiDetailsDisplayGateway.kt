package com.luckyzyx.luckytool.hook.scopes.wirelesssettings

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiManager
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.luckyzyx.commonutils.formatStringAuto
import com.luckyzyx.luckytool.hook.utils.preferences.PreferenceReflections
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import java.net.Inet4Address
import java.net.Inet6Address

@Obfuscate
class EnableWifiDetailsDisplayGateway(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source WifiAddressController
        val clazz = VariousClass(
            "com.oplus.wirelesssettings.wifi.detail.WifiAddressController", //C12
            "com.oplus.wirelesssettings.wifi.detail2.WifiAddressController" //C13 C14 C15
        ).toClass()
        dexKitBridge.findClass {
            matcher {
                className(clazz.name)
            }
        }.apply {
            checkDataList("EnableWifiDetailsDisplayGateway Controller")
            findMethod {
                matcher {
                    paramCount(0)
                    returnType(BooleanType)
                    usingStrings("updateIpInfo")
                }
            }.apply {
                checkDataList("EnableWifiDetailsDisplayGateway Summary")
                single().className.toClass().apply {
                    method {
                        name = single().methodName
                        emptyParam()
                        returnType = BooleanType
                    }.hook {
                        after {
                            val context = field { type = ContextClass }.get(instance)
                                .cast<Context>() ?: return@after
                            val preferenceScreen = field {
                                type = "androidx.preference.PreferenceScreen"
                                superClass()
                            }.get(instance).any() ?: return@after

                            val connectivityManager =
                                context.getSystemService(ConnectivityManager::class.java)
                            val wifiManager =
                                context.applicationContext.getSystemService(WifiManager::class.java)

                            val getCurrentNetwork = wifiManager.current().method {
                                name = "getCurrentNetwork"
                                emptyParam()
                                returnType = Network::class.java
                            }.invoke<Network>() ?: return@after

                            @SuppressLint("MissingPermission")
                            val linkProperties =
                                connectivityManager.getLinkProperties(getCurrentNetwork)
                                    ?: return@after

                            var ipv4Gateway = ""
                            var ipv6Gateway = ""

                            linkProperties.routes.forEachIndexed { _, routeInfo ->
                                if (routeInfo.isDefaultRoute) {
                                    val gateway = routeInfo.gateway ?: return@forEachIndexed
                                    val hostAddress = gateway.hostAddress ?: return@forEachIndexed
                                    if (routeInfo.destination.address is Inet4Address) {
                                        ipv4Gateway = hostAddress
                                    }
                                    if (routeInfo.destination.address is Inet6Address) {
                                        ipv6Gateway = hostAddress
                                    }
                                }
                            }

                            val ipv4 = PreferenceReflections.findPreference(
                                preferenceScreen, "current_ipv4_address"
                            ) ?: return@after
                            if (PreferenceReflections.isVisible(ipv4)) {
                                val summary = PreferenceReflections.getSummary(ipv4)
                                PreferenceReflections.setSummary(
                                    ipv4, formatStringAuto(
                                        arrayListOf(summary, "", ipv4Gateway), "\n"
                                    )
                                )
                            }

                            val ipv6 = PreferenceReflections.findPreference(
                                preferenceScreen, "current_ipv6_address"
                            ) ?: return@after
                            if (PreferenceReflections.isVisible(ipv6)) {
                                val summary = PreferenceReflections.getSummary(ipv6)
                                PreferenceReflections.setSummary(
                                    ipv6, formatStringAuto(
                                        arrayListOf(summary, "", ipv6Gateway), "\n"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}