package com.luckyzyx.luckytool.hook.scopes.otherapp

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookBili : YukiBaseHooker() {
    override fun onHook() {
        //移除开屏广告
        if (prefs(ModulePrefs).getBoolean("remove_bili_splash_ads", false)) {
            loadHooker(RemoveBiliSplashAds)
        }
    }

    @Obfuscate
    object RemoveBiliSplashAds : YukiBaseHooker() {
        override fun onHook() {
            //Source Splash
            "tv.danmaku.bili.ui.splash.ad.model.Splash".toClass().resolve().apply {
                firstMethod { name = "isValid" }.hook {
                    replaceToFalse()
                }
            }
        }
    }
}