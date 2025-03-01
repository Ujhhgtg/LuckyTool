package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object AppSplashScreen : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("disable_splash_screen", false)

        //Source StartingSurfaceController
        "com.android.server.wm.StartingSurfaceController".toClass().apply {
            method { name = "showStartingWindow";paramCount = 5 }.hook {
                if (isEnable) intercept()
            }
        }
    }
}