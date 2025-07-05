package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookGlobalFeatureConfig
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookEyeProtect : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalFeatureConfig)
    }
}