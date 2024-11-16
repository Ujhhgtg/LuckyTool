package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalFeatureConfig

@Obfuscate
object HookOplusMMS : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)
    }
}