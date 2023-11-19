package com.luckyzyx.luckytool.hook.scope.weather

import android.content.Context
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.PendingIntentClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import org.luckypray.dexkit.DexKitBridge

class WeatherAdsAndJumpBrowser(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        if (SDK >= A13) loadHooker(HookWeatherAdsAndJump)
        else loadHooker(HookWeatherAdsAndJumpC12(dexKitBridge))
    }

    object HookWeatherAdsAndJump : YukiBaseHooker() {
        private const val weatherWrapper = "com.oplus.weather.main.model.WeatherWrapper"
        override fun onHook() {
            val removeAds =
                prefs(ModulePrefs).getBoolean("remove_weather_some_page_bottom_ads", false)
            val disableJump = prefs(ModulePrefs).getBoolean("disable_weather_jump_browser", false)
            if (!removeAds && !disableJump) return

            //Source LocalUtils
            "com.oplus.weather.utils.LocalUtils".toClass().apply {
                method { name = "jumpToBrowser" }.hookAll {
                    hookBefore(removeAds, disableJump)
                }
                method { name = "startBrowserForUrl" }.hookAll {
                    hookBefore(removeAds, disableJump)
                }
            }

            //Source NoticeItem C14
            "com.oplus.weather.main.view.itemview.NoticeItem".toClassOrNull()?.apply {
                method { name = "showRainfallPanel" }.hook {
                    before {
                        if (!disableJump) return@before
                        val wrapper = field { type = weatherWrapper }.get(instance).any()
                            ?: return@before
                        wrapper.current().method { name = "setRainFallAdLink" }.call("")
                    }
                }
                method { name = "showWarnWeatherPanel" }.hook {
                    before {
                        if (!disableJump) return@before
                        val warnInfo = args().last().any() ?: return@before
                        warnInfo.current().field { name = "addLink" }.set("")
                    }
                }
            }

            //Source SecondaryPageUtil
            "com.oplus.weather.utils.SecondaryPageUtil".toClassOrNull()?.apply {
                method { name = "newLink" }.hook {
                    after {
                        if (removeAds) result = formatWeatherUrl(
                            result<String>() ?: return@after
                        )
                    }
                }
            }

            //Source RainReminder -> Channel com.oplus.weather.service.rain
            "com.oplus.weather.service.service.RainReminder".toClass().apply {
                method { name = "createIntentOpenWeatherMainActivity" }.hook {
                    before {
                        if (disableJump) args().last().set("")
                    }
                }
            }
            //Source WarnReminder -> Channel oppo.oplus.weather.warnWeather
            "com.oplus.weather.service.service.WarnReminder".toClass().apply {
                method { name = "getWarnWeatherIntent" }.hook {
                    before {
                        if (disableJump) args().last().set("")
                    }
                }
            }
            //Source MorningReminder -> Channel oppo.oplus.weather.morningWeather
            "com.oplus.weather.morning.MorningReminder".toClass().apply {
                method {
                    param(weatherWrapper, ContextClass)
                    returnType(PendingIntentClass)
                }.hook {
                    before { if (disableJump) resultNull() }
                }
            }
        }

        private fun YukiMemberHookCreator.MemberHookCreator.hookBefore(
            removeAds: Boolean, disableJump: Boolean
        ) {
            before {
                val context = args.find { it is Context } ?: return@before
                val url = args(2).cast<String>()
                if (url.isNullOrBlank()) return@before
                val statisticsTag = args(3).cast<String>()
                if (statisticsTag.isNullOrBlank()) return@before

                //CCTV
                if (url.startsWith("heytapbrowser://")) return@before

                if (removeAds) args(2).set(formatWeatherUrl(url))
                if (disableJump) {
                    val newUrl = args(2).cast<String>()
                    if (newUrl.isNullOrBlank()) return@before
                    val clazz = "com.oplus.weather.plugin.webview.BrowserCommonUtils"
                    startWebActivity(clazz, context, newUrl, statisticsTag)
                    resultNull()
                }
            }
        }
    }

    class HookWeatherAdsAndJumpC12(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        private var startWebView = ""
        override fun onHook() {
            val removeAds =
                prefs(ModulePrefs).getBoolean("remove_weather_some_page_bottom_ads", false)
            val disableJump = prefs(ModulePrefs).getBoolean("disable_weather_jump_browser", false)
            if (!removeAds && !disableJump) return

            //Source OppoUtils
            dexKitBridge.findClass {
                searchPackages("com.coloros.weather.utils")
                matcher {
                    fields {
                        addForType(BooleanType.name)
                        addForType("java.util.regex.Pattern")
                    }
                    methods {
                        add {
                            paramTypes(
                                IntType, ContextClass, StringClass,
                                StringClass, BooleanType, BooleanType
                            )
                            returnType(UnitType)
                            usingStrings(
                                "OppoUtils", "frontCode", "infoEnable", "fromWeatherApp"
                            )
                        }
                        add {
                            paramTypes(
                                ContextClass, IntType, StringClass,
                                StringClass, BooleanType
                            )
                            returnType(UnitType)
                            usingStrings(
                                "com.heytap.browser",
                                "com.android.browser",
                                "com.coloros.browser"
                            )
                        }

                    }
                }
            }.apply {
                checkDataList("HookWeatherAdsAndJumpC12 OppoUtils")
                first().name.toClass().apply {
                    method {
                        param(
                            IntType, ContextClass, StringClass,
                            StringClass, BooleanType, BooleanType
                        )
                        returnType(UnitType)
                    }.hookAll { hookBefore(removeAds, disableJump) }
                    method {
                        param(
                            ContextClass, IntType, StringClass, StringClass, BooleanType
                        )
                        returnType(UnitType)
                    }.hookAll { hookBefore(removeAds, disableJump) }
                }
            }
            dexKitBridge.findMethod {
                searchPackages("com.coloros.weather.plugin.webview")
                matcher {
                    paramCount(5)
                    returnType(UnitType)
                    usingNumbers(536870912)
                    usingStrings(
                        "context", "url", "statisticsTag",
                        "intent_params_url", "intent_params_isFirst", "intent_params_statistics"
                    )
                }
            }.apply {
                checkDataList("HookWeatherAdsAndJumpC12 BrowserCommonUtils")
                startWebView = first().className
            }
        }

        private fun YukiMemberHookCreator.MemberHookCreator.hookBefore(
            removeAds: Boolean, disableJump: Boolean
        ) {
            before {
                val context = args.find { it is Context } ?: return@before
                val url = args(2).cast<String>()
                if (url.isNullOrBlank()) return@before
                val statisticsTag = args(3).cast<String>()
                if (statisticsTag.isNullOrBlank()) return@before

                //CCTV
                if (url.startsWith("heytapbrowser://")) return@before

                if (removeAds) args(2).set(formatWeatherUrl(url))
                if (disableJump) {
                    val newUrl = args(2).cast<String>()
                    if (newUrl.isNullOrBlank()) return@before
                    val clazz = startWebView.takeIf { it.isNotBlank() } ?: return@before
                    startWebActivity(clazz, context, newUrl, statisticsTag)
                    resultNull()
                }
            }
        }
    }

    companion object {
        fun startWebActivity(clazz: String, context: Any, url: String, statisticsTag: String) {
            //Source BrowserCommonUtils -> startWeatherWebActivity
            clazz.toClass().method { paramCount = 5 }.get()
                .call(context, url, true, statisticsTag, true)
        }

        fun formatWeatherUrl(url: String): String {
            if (url.isBlank()) return url
            var cacheUrl = url
            if (cacheUrl.contains("fromWeatherApp=true")) cacheUrl = cacheUrl.replace(
                "fromWeatherApp=true", "fromWeatherApp=false"
            )
            if (cacheUrl.contains("infoEnable=true")) cacheUrl = cacheUrl.replace(
                "infoEnable=true", "infoEnable=false"
            )
            if (cacheUrl.contains("infoEnable").not()) cacheUrl += "&infoEnable=false"
            return cacheUrl
        }
    }
}