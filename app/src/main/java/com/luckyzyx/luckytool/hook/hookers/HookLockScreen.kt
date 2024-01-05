package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.ForceEnableScreenOffMusicSupport
import com.luckyzyx.luckytool.hook.scopes.systemui.HideLockScreenStatusBarDisplay
import com.luckyzyx.luckytool.hook.scopes.systemui.LockScreenBottomButton
import com.luckyzyx.luckytool.hook.scopes.systemui.LockScreenCarriers
import com.luckyzyx.luckytool.hook.scopes.systemui.LockScreenChargingComponent
import com.luckyzyx.luckytool.hook.scopes.systemui.LockScreenClock
import com.luckyzyx.luckytool.hook.scopes.systemui.LockScreenComponentStyle
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveAodMusicWhitelist
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveLockScreenBottomSOSButton
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveLockScreenCloseNotificationButton
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveTopLockScreenIcon
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object HookLockScreen : YukiBaseHooker() {
    override fun onHook() {
        //锁屏时钟
        loadHooker(LockScreenClock)

        //锁屏组件样式
        loadHooker(LockScreenComponentStyle)

        //锁屏充电组件
        loadHooker(LockScreenChargingComponent)

        //锁屏底部按钮
        loadHooker(LockScreenBottomButton)

        //锁屏状态栏运营商
        loadHooker(LockScreenCarriers)

        //隐藏锁屏状态栏显示
        if (prefs(ModulePrefs).getBoolean("hide_lock_screen_status_bar_display", false)) {
            loadHooker(HideLockScreenStatusBarDisplay)
        }
        //移除SOS紧急联络按钮
        if (prefs(ModulePrefs).getBoolean("remove_lock_screen_bottom_sos_button", false)) {
            if (SDK >= A13) loadHooker(RemoveLockScreenBottomSOSButton)
        }
        //移除锁屏顶部图标
        if (prefs(ModulePrefs).getBoolean("remove_top_lock_screen_icon", false)) {
            loadHooker(RemoveTopLockScreenIcon)
        }
        //移除锁屏关闭通知按钮
        if (prefs(ModulePrefs).getBoolean("remove_lock_screen_close_notification_button", false)) {
            loadHooker(RemoveLockScreenCloseNotificationButton)
        }
        //移除息屏音乐白名单
        if (prefs(ModulePrefs).getBoolean("remove_aod_music_whitelist", false)) {
            if (SDK >= A13) loadHooker(RemoveAodMusicWhitelist)
        }
        //强制启用息屏音乐支持
        if (prefs(ModulePrefs).getBoolean("force_enable_screen_off_music_support", false)) {
            if (SDK >= A13) loadHooker(ForceEnableScreenOffMusicSupport)
        }
    }
}