package com.luckyzyx.luckytool.hook.scopes.weather

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.android.PendingIntentClass
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.data.AppVerInfo
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType

@Obfuscate
class WeatherAdsAndJumpBrowser(
    private val appVer: AppVerInfo?, val dexKitBridge: DexKitBridge
) : YukiBaseHooker() {
    override fun onHook() {
        val isNew = appVer?.versionCode?.let { it >= 13000000 } ?: return
        if (isNew) loadHooker(HookWeatherAdsAndJump)
        else loadHooker(HookWeatherAdsAndJumpC12(dexKitBridge))
    }

    @Obfuscate
    object HookWeatherAdsAndJump : YukiBaseHooker() {
        private const val weatherWrapper = "com.oplus.weather.main.model.WeatherWrapper"
        private const val BrowserCommonUtils = "com.oplus.weather.plugin.webview.BrowserCommonUtils"
        override fun onHook() {
            val removeAds =
                prefs(ModulePrefs).getBoolean("remove_weather_some_page_bottom_ads", false)
            val disableJump = prefs(ModulePrefs).getBoolean("disable_weather_jump_browser", false)
            if (!removeAds && !disableJump) return

            //Source OPPOFeedAdManager switchesPopularRecommended
            "com.oplus.weather.ad.OPPOFeedAdManager".toClassOrNull()?.apply {
                method { name = "hasOpenPopularRecommended" }.hook {
                    if (removeAds) replaceToFalse()
                }
                method { name = "hasOpenAdSdkShowBannerFromNetwork" }.hook {
                    if (removeAds) replaceToFalse()
                }
            }

            //Source AppFeatureUtils
            "com.oplus.weather.utils.AppFeatureUtils".toClassOrNull()?.apply {
                method { name = "isSupportOplusAd" }.hook {
                    if (removeAds) replaceToFalse()
                }
            }

            //Source LocalUtils
            "com.oplus.weather.utils.LocalUtils".toClass().apply {
                method { name = "jumpToBrowser" }.hookAll {
                    hookBefore(removeAds, disableJump)
                }
                method { name = "startBrowserForUrl" }.hookAll {
                    hookBefore(removeAds, disableJump)
                }
                if (hasMethod { name = "isBrowserSupportJump" }) {
                    method { name = "isBrowserSupportJump" }.hook {
                        replaceToFalse()
                    }
                }
            }

            //Source NoticeItem C14
            "com.oplus.weather.main.view.itemview.NoticeItem".toClassOrNull()?.apply {
                method { name = "showRainfallPanel" }.hook {
                    before {
                        if (!disableJump) return@before
                        val wrapper =
                            field { type = weatherWrapper }.get(instance).any() ?: return@before
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
                method {
                    name { it.startsWith("jump") && it.contains("Browser") }
                    param { it.contains(StringClass) && it.contains(BooleanType) }
                    returnType { it == IntentClass || it == AnyClass }
                }.hookAll {
                    after {
                        val intent = result<Intent>() ?: return@after
                        intent.data = formatWeatherUrl(intent.data.toString()).toUri()
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
            //Source MorningReminder -> Channel oppo.oplus.weather.morningWeather C13.1
            "com.oplus.weather.morning.MorningReminder".toClassOrNull()?.apply {
                method {
                    param(weatherWrapper, ContextClass)
                    returnType(PendingIntentClass)
                }.hook {
                    before {
                        if (disableJump) resultNull()
                    }
                }
            }
        }

        private fun YukiMemberHookCreator.MemberHookCreator.hookBefore(
            removeAds: Boolean, disableJump: Boolean
        ) {
            before {
                val context = args.find { it is Context } ?: return@before
                val url = args(2).string()
                val statisticsTag = args(3).string()

                //CCTV
                if (url.startsWith("heytapbrowser://")) return@before

                if (removeAds) args(2).set(formatWeatherUrl(url))
                if (disableJump) {
                    val newUrl = args(2).string()
                    startWebActivity(BrowserCommonUtils.toClass(), context, newUrl, statisticsTag)
                    resultNull()
                }
            }
        }
    }

    @Obfuscate
    class HookWeatherAdsAndJumpC12(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
        private var startWebView = ""
        override fun onHook() {
            val removeAds =
                prefs(ModulePrefs).getBoolean("remove_weather_some_page_bottom_ads", false)
            val disableJump =
                prefs(ModulePrefs).getBoolean("disable_weather_jump_browser", false)
            if (!removeAds && !disableJump) return

            //Source OppoUtils
            dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(BooleanType)
                        addForType("java.util.regex.Pattern")
                    }
                    methods {
                        add {
                            paramTypes(
                                IntType,
                                ContextClass,
                                StringClass,
                                StringClass,
                                BooleanType,
                                BooleanType
                            )
                            returnType(UnitType)
                            usingStrings(
                                "OppoUtils", "frontCode", "infoEnable", "fromWeatherApp"
                            )
                        }
                        add {
                            paramTypes(
                                ContextClass, IntType, StringClass, StringClass, BooleanType
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
                single().name.toClass().apply {
                    method {
                        param(
                            IntType,
                            ContextClass,
                            StringClass,
                            StringClass,
                            BooleanType,
                            BooleanType
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
            dexKitBridge.findClass {
                matcher {
                    className("com.coloros.weather.plugin.webview", StringMatchType.StartsWith)
                }
            }.findMethod {
                matcher {
                    paramCount(5)
                    returnType(UnitType)
                    usingNumbers(536870912)
                    usingStrings(
                        "context",
                        "url",
                        "statisticsTag",
                        "intent_params_url",
                        "intent_params_isFirst",
                        "intent_params_statistics"
                    )
                }
            }.apply {
                checkDataList("HookWeatherAdsAndJumpC12 BrowserCommonUtils")
                startWebView = single().className
            }
        }

        private fun YukiMemberHookCreator.MemberHookCreator.hookBefore(
            removeAds: Boolean, disableJump: Boolean
        ) {
            before {
                val context = args.find { it is Context } ?: return@before
                val url = args(2).string()
                val statisticsTag = args(3).string()

                //CCTV
                if (url.startsWith("heytapbrowser://")) return@before

                if (removeAds) args(2).set(formatWeatherUrl(url))
                if (disableJump) {
                    val newUrl = args(2).string()
                    val clazz = startWebView.takeIf { it.isNotBlank() } ?: return@before
                    startWebActivity(clazz.toClass(), context, newUrl, statisticsTag)
                    resultNull()
                }
            }
        }
    }

    companion object {
        fun startWebActivity(
            clazz: Class<*>,
            context: Any,
            url: String,
            statisticsTag: String
        ) {
            //Source BrowserCommonUtils -> startWeatherWebActivity
            clazz.method { paramCount = 5 }.get().call(context, url, true, statisticsTag, true)
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