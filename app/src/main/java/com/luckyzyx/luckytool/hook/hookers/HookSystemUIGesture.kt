package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.FullScreenGestureSideSlideBar
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveRotateScreenButton
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookSystemUIGesture : YukiBaseHooker() {
    override fun onHook() {
        //全面屏手势侧滑条
        loadHooker(FullScreenGestureSideSlideBar)

        //移除旋转屏幕按钮
        if (prefs(ModulePrefs).getBoolean("remove_rotate_screen_button", false)) {
            loadHooker(RemoveRotateScreenButton)
        }
    }
}