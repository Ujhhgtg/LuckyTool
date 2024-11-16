package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.weather.Enable15DayWeatherExpandList
import com.luckyzyx.luckytool.hook.scopes.weather.RestoreRainfallCloudMapPage
import com.luckyzyx.luckytool.hook.scopes.weather.WeatherAdsAndJumpBrowser
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getAppVerInfo

@Obfuscate
object HookWeather : YukiBaseHooker() {
    override fun onHook() {
        val appVer = prefs(ModulePrefs).getAppVerInfo(packageName)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //天气广告与跳转浏览器
            loadHooker(WeatherAdsAndJumpBrowser(appVer, dexKitBridge))
        }

        //启用15日天气展开列表
        if (prefs(ModulePrefs).getBoolean("enable_15_day_weather_expand_list", false)) {
            loadHooker(Enable15DayWeatherExpandList)
        }
        //恢复降雨云图页面
        if (prefs(ModulePrefs).getBoolean("restore_rainfall_cloud_map_page", false)) {
            loadHooker(RestoreRainfallCloudMapPage)
        }
    }
}