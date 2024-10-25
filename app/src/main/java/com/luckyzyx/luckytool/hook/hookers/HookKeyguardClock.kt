package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.keyguardclock.LockScreenClockRedMode

object HookKeyguardClock : YukiBaseHooker() {
    override fun onHook() {
        //锁屏时钟
        loadHooker(LockScreenClockRedMode)
    }
}