package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.keyguardclock.KeyGuardcLockRedMode
import com.luckyzyx.luckytool.utils.DexkitUtils

@Obfuscate
object HookKeyguardClock : YukiBaseHooker() {
    override fun onHook() {

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //锁屏时钟
            loadHooker(KeyGuardcLockRedMode(dexKitBridge))
        }

    }
}