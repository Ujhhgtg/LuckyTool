package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object HookAtlasService : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalSystemProperties)
    }
}