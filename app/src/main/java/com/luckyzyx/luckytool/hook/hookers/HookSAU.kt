package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalSystemProperties
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookSAU : YukiBaseHooker(){
    override fun onHook() {
        loadHooker(HookGlobalSystemProperties)
    }
}