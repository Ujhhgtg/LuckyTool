package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.keyguardclock.LockScreenClockRedMode

@Obfuscate
object HookKeyguardClock : YukiBaseHooker() {
    override fun onHook() {
        //锁屏时钟
        loadHooker(LockScreenClockRedMode)
    }
}