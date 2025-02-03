package com.luckyzyx.luckytool.hook.scopes.android

import android.os.Build
import android.util.ArraySet
import com.android.internal.os.ClassLoaderFactory
import com.android.internal.os.SystemServerClassLoaderFactory
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.StringArrayClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object HookOplusWifiService : YukiBaseHooker() {

    private var wifiserviceClassLoader: ClassLoader? = null
    private var finalWifiServiceClassLoader: ClassLoader? = null

    private val wifiService = "/apex/com.android.wifi/javalib/service-wifi.jar"
    private val oplusWifiService = "/system_ext/framework/oplus-wifi-service.jar"

    override fun onHook() {
        try {
            wifiserviceClassLoader = SystemServerClassLoaderFactory.getOrCreateClassLoader(
                wifiService, null, false
            )
            finalWifiServiceClassLoader = SystemServerClassLoaderFactory.getOrCreateClassLoader(
                oplusWifiService, wifiserviceClassLoader, false
            )
        } catch (t: ClassNotFoundException) {
            YLog.error("Oplus Wifi Service Error!", t)
            try {
                wifiserviceClassLoader = ClassLoaderFactory.createClassLoader(
                    wifiService, null, null, null,
                    Build.VERSION.SDK_INT, true, null
                )
                finalWifiServiceClassLoader = ClassLoaderFactory.createClassLoader(
                    wifiService, null, null, wifiserviceClassLoader,
                    Build.VERSION.SDK_INT, true, null
                )
            } catch (_: Throwable) {
                YLog.error("Wifi Service Error!", t)
            }
        } catch (t: Throwable) {
            YLog.error("Oplus Wifi Service Error!", t)
        }

        if (finalWifiServiceClassLoader == null) {
            YLog.error("Hook Oplus Wifi Service is null!")
            return
        }

        //Source_ext oplus-wifi-service OplusTetheringNotification showSoftapEnabledDurationNotification
        //Channel DurationNotification -> Notification id -> 4
        if (prefs(ModulePrefs).getBoolean("remove_hotspot_power_consumption_notification", false)) {
            loadHooker(HookOplusSoftAp(finalWifiServiceClassLoader))
        }

        //Source_ext oplus-wifi-service OplusWifiRomUpdateHelper getSlaWhiteListApps
        loadHooker(HookSlaAppList(finalWifiServiceClassLoader))
    }

    @Obfuscate
    class HookOplusSoftAp(val classLoader: ClassLoader?) : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusSoftapStatistics
            "com.oplus.server.wifi.hotspot.OplusSoftapStatistics".toClass(classLoader).apply {
                method { name = "startSoftapEnableTimer" }.hook {
                    intercept()
                }
            }
        }
    }

    @Obfuscate
    class HookSlaAppList(val classLoader: ClassLoader?) : YukiBaseHooker() {
        override fun onHook() {
            var mode = prefs(ModulePrefs).getString("set_wlan_sla_whitelist_mode", "0")
            dataChannel.wait<String>("set_wlan_sla_whitelist_mode") { mode = it }
            var rmBlack = prefs(ModulePrefs).getBoolean("remove_wlan_sla_blacklist", false)
            dataChannel.wait<Boolean>("remove_wlan_sla_blacklist") { rmBlack = it }
            var set = prefs(ModulePrefs).getStringSet("custom_wlan_sla_whitelist", ArraySet())
            dataChannel.wait<Set<String>>("custom_wlan_sla_whitelist") { set = it }
            var gameSet =
                prefs(ModulePrefs).getStringSet("custom_wlan_sla_game_whitelist", ArraySet())
            dataChannel.wait<Set<String>>("custom_wlan_sla_game_whitelist") { gameSet = it }

            if (mode == "0") return

            //Source OplusSlaApps
            VariousClass(
                "com.oplus.server.wifi.OplusSlaApps", //C13
                "com.oplus.server.wifi.sla.OplusSlaApps" //C14 C15
            ).toClass(classLoader).apply {
                method { name = "getSlaWhiteListAppsFromRus";returnType = StringArrayClass }.hook {
                    after {
                        val res = result<Array<String>>() ?: return@after
                        result = when (mode) {
                            "1" -> res.toMutableList().apply {
                                set.forEachIndexed { _, new ->
                                    if (!contains(new)) add(new)
                                }
                            }.toTypedArray()

                            "2" -> set.toTypedArray()
                            else -> return@after
                        }
                    }
                }
                method { name = "getSlaGameAppsFromRus";returnType = StringArrayClass }.hook {
                    after {
                        val res = result<Array<String>>() ?: return@after
                        result = when (mode) {
                            "1" -> res.toMutableList().apply {
                                gameSet.forEachIndexed { _, new ->
                                    if (!contains(new)) add(new)
                                }
                            }.toTypedArray()

                            "2" -> gameSet.toTypedArray()
                            else -> return@after
                        }
                    }
                }
                method { name = "getSlaBlackListAppsFromRus" }.hook {
                    before {
                        if (rmBlack) resultNull()
                    }
                }
            }
        }
    }
}