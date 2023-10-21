package com.luckyzyx.luckytool.hook.scope.weather

import android.content.Context
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object WeatherAdsAndJumpBrowser : YukiBaseHooker() {
    override fun onHook() {
        val removeAds = prefs(ModulePrefs).getBoolean("remove_weather_some_page_bottom_ads", false)
        val disableJump = prefs(ModulePrefs).getBoolean("disable_weather_jump_browser", false)
        if (!removeAds && !disableJump) return

        //Source LocalUtils
        "com.oplus.weather.utils.LocalUtils".toClass().apply {
            method { name = "jumpToBrowser" }.hookAll { hookBefore(removeAds, disableJump) }
            method { name = "startBrowserForUrl" }.hookAll { hookBefore(removeAds, disableJump) }
        }

        //Source NoticeItem C14
        "com.oplus.weather.main.view.itemview.NoticeItem".toClassOrNull()?.apply {
            method { name = "showRainfallPanel" }.hook {
                before {
                    if (!disableJump) return@before
                    val wrapper = field { type = "com.oplus.weather.main.model.WeatherWrapper" }
                        .get(instance).any() ?: return@before
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
                startWebActivity(context, newUrl, statisticsTag)
                resultNull()
            }
        }
    }

    private fun startWebActivity(context: Any, url: String, statisticsTag: String) {
        //Source BrowserCommonUtils
        "com.oplus.weather.plugin.webview.BrowserCommonUtils".toClass().method {
            name = "startWeatherWebActivity";paramCount = 5
        }.get().call(context, url, true, statisticsTag, true)
    }

    private fun formatWeatherUrl(url: String): String {
        return when {
            url.contains("infoEnable=true") -> url.replace(
                "infoEnable=true", "infoEnable=false"
            )

            url.contains("infoEnable").not() -> "${url}&infoEnable=false"
            else -> url
        }
    }
}