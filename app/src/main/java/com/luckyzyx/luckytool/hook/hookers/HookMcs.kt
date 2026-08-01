package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookGlobalSystemProperties

object HookMcs : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalSystemProperties)
    }
}