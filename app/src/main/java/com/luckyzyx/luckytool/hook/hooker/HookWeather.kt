package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.weather.WeatherAdsAndJumpBrowser
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.SDK

object HookWeather : YukiBaseHooker() {
    override fun onHook() {
        if (SDK < A13) return

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //天气广告与跳转浏览器
            loadHooker(WeatherAdsAndJumpBrowser(dexKitBridge))
        }
    }
}