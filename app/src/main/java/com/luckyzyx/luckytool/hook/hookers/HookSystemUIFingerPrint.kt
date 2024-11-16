package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.systemui.FingerPrintIconAnim

@Obfuscate
object HookSystemUIFingerPrint : YukiBaseHooker() {
    override fun onHook() {
        //指纹图标
        loadHooker(FingerPrintIconAnim)
    }
}