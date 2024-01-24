package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalSystemProperties

object HookAtlasService : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalSystemProperties)
    }
}