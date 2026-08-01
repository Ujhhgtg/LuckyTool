package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookGlobalSystemProperties

object HookMyDevices : YukiBaseHooker() {
    override fun onHook() {

        loadHooker(HookGlobalSystemProperties)

    }
}