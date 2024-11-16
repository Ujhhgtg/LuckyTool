package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.systemui.HookSystemUIFeature

@Obfuscate
object HookSystemUI : YukiBaseHooker() {
    override fun onHook() {
        //系统界面Feature
        loadHooker(HookSystemUIFeature)

        //状态栏功能
        loadHooker(HookSystemUIStatusBar)

        //锁屏
        loadHooker(HookSystemUILockScreen)

        //对话框相关
        loadHooker(HookSystemUIDialog)

        //全面屏手势相关
        loadHooker(HookSystemUIGesture)

        //指纹相关
        loadHooker(HookSystemUIFingerPrint)

        //杂项
        loadHooker(HookSystemUiMiscellaneous)

        //自启
        loadHooker(HookSystemUIAutoStart)

    }
}