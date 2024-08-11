package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.mediacontroller.ForceEnableMediaMusicFluidCloudRipple
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

object HookMediaController : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        //强制启用媒体音乐流体云波纹
        if (prefs(ModulePrefs).getBoolean("force_enable_media_music_fluid_cloud_ripple", false)) {
            if (osCode >= 33) loadHooker(ForceEnableMediaMusicFluidCloudRipple)
        }
    }
}