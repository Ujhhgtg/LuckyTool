package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.weather.WeatherAdsAndJumpBrowser

object HookWeather : YukiBaseHooker() {
    override fun onHook() {
        //天气广告与跳转浏览器
        loadHooker(WeatherAdsAndJumpBrowser)
    }
}