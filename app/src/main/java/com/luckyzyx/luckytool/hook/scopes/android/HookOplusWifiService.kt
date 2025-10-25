package com.luckyzyx.luckytool.hook.scopes.android

import android.os.Build
import android.util.ArraySet
import com.android.internal.os.ClassLoaderFactory
import com.android.internal.os.SystemServerClassLoaderFactory
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.ArrayClass
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookOplusWifiService : YukiBaseHooker() {

    private var wifiserviceClassLoader: ClassLoader? = null
    private var finalWifiServiceClassLoader: ClassLoader? = null

    private val wifiServicePath = "/apex/com.android.wifi/javalib/service-wifi.jar"
    private val oplusWifiServicePath = "/system_ext/framework/oplus-wifi-service.jar"

    private fun initClassLoader() {
        try {
            wifiserviceClassLoader = SystemServerClassLoaderFactory.getOrCreateClassLoader(
                wifiServicePath, null, false
            )
            finalWifiServiceClassLoader = SystemServerClassLoaderFactory.getOrCreateClassLoader(
                oplusWifiServicePath, wifiserviceClassLoader, false
            )
        } catch (_: Throwable) {
            try {
                wifiserviceClassLoader = ClassLoaderFactory.createClassLoader(
                    wifiServicePath, null, null, null,
                    Build.VERSION.SDK_INT, true, null
                )
                finalWifiServiceClassLoader = ClassLoaderFactory.createClassLoader(
                    wifiServicePath, null, null, wifiserviceClassLoader,
                    Build.VERSION.SDK_INT, true, null
                )
            } catch (t: Throwable) {
                YLog.error("Hook Wifi Service Error!", t)
            }
        }

        if (finalWifiServiceClassLoader == null) {
            YLog.error("Hook Oplus Wifi Service is null!")
            return
        }
    }

    override fun onHook() {
        initClassLoader()

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
            "com.oplus.server.wifi.hotspot.OplusSoftapStatistics".toClass(classLoader).resolve()
                .apply {
                    firstMethod { name = "startSoftapEnableTimer" }.hook {
                        intercept()
                    }
                }
        }
    }

    @Obfuscate
    class HookSlaAppList(val classLoader: ClassLoader?) : YukiBaseHooker() {

        private val whitelistKey = "custom_wlan_sla_whitelist"
        private val gameWhitelistKey = "custom_wlan_sla_game_whitelist"

        var mode = "0"
        var rmBlack = false
        val whitelist = ArraySet<String>()
        val gameWhitelist = ArraySet<String>()

        private fun initData() {
            mode = prefs(ModulePrefs).getString("set_wlan_sla_whitelist_mode", "0")
            dataChannel.wait<String>("set_wlan_sla_whitelist_mode") {
                mode = it
                YLog.debug("update oplus wifi configs status -> $it")
            }
            rmBlack = prefs(ModulePrefs).getBoolean("remove_wlan_sla_blacklist", false)
            dataChannel.wait<Boolean>("remove_wlan_sla_blacklist") { rmBlack = it }

            whitelist.clear()
            whitelist.addAll(prefs(ModulePrefs).getStringSet(whitelistKey, ArraySet()))
            dataChannel.wait(whitelistKey) {
                val new = prefs(ModulePrefs).getStringSet(whitelistKey, ArraySet())
                YLog.debug("update oplus wifi whitelist configs -> ${whitelist.size} | ${new.size}")
                whitelist.clear()
                whitelist.addAll(new)
            }

            gameWhitelist.clear()
            gameWhitelist.addAll(prefs(ModulePrefs).getStringSet(gameWhitelistKey, ArraySet()))
            dataChannel.wait(gameWhitelistKey) {
                val new = prefs(ModulePrefs).getStringSet(gameWhitelistKey, ArraySet())
                YLog.debug("update oplus wifi game whitelist configs -> ${gameWhitelist.size} | ${new.size}")
                gameWhitelist.clear()
                gameWhitelist.addAll(new)
            }
            YLog.debug("init oplus wifi configs success -> ${whitelist.size} | ${gameWhitelist.size}")
        }

        override fun onHook() {
            initData()

            if (mode == "0") return

            //Source OplusSlaApps
            VariousClass(
                "com.oplus.server.wifi.OplusSlaApps", //C13
                "com.oplus.server.wifi.sla.OplusSlaApps" //C14 C15
            ).toClass(classLoader).resolve().apply {
                firstMethod {
                    name = "getSlaWhiteListAppsFromRus"
                    returnType = ArrayClass(String::class.java)
                }.hook {
                    after {
                        if (mode == "0") return@after
                        val res = result<Array<String>>() ?: return@after
                        result = when (mode) {
                            "1" -> res.toMutableList().apply {
                                whitelist.forEachIndexed { _, new ->
                                    if (!contains(new)) add(new)
                                }
                            }.toTypedArray()

                            "2" -> whitelist.toTypedArray()
                            else -> return@after
                        }
                    }
                }
                firstMethod {
                    name = "getSlaGameAppsFromRus"
                    returnType = ArrayClass(String::class.java)
                }.hook {
                    after {
                        if (mode == "0") return@after
                        val res = result<Array<String>>() ?: return@after
                        result = when (mode) {
                            "1" -> res.toMutableList().apply {
                                gameWhitelist.forEachIndexed { _, new ->
                                    if (!contains(new)) add(new)
                                }
                            }.toTypedArray()

                            "2" -> gameWhitelist.toTypedArray()
                            else -> return@after
                        }
                    }
                }
                firstMethod { name = "getSlaBlackListAppsFromRus" }.hook {
                    before {
                        if (mode == "0") return@before
                        if (rmBlack) resultNull()
                    }
                }
            }
        }
    }
}