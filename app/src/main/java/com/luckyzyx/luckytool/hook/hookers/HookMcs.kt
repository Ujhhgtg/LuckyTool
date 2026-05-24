package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookGlobalSystemProperties
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookMcs : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(HookGlobalSystemProperties)
    }
}