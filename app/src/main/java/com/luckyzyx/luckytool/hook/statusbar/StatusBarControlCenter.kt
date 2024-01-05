package com.luckyzyx.luckytool.hook.statusbar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.ControlCenterBackgroundTransParency
import com.luckyzyx.luckytool.hook.scopes.systemui.ControlCenterClockStyle
import com.luckyzyx.luckytool.hook.scopes.systemui.ControlCenterDateStyle
import com.luckyzyx.luckytool.hook.scopes.systemui.EnableNotificationAlignBothSides
import com.luckyzyx.luckytool.hook.scopes.systemui.NotificationBackgroundTransParency
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveControlCenterUserSwitcher
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveStatusBarBottomNetworkWarn
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object StatusBarControlCenter : YukiBaseHooker() {
    override fun onHook() {
        //控制中心时钟样式
        loadHooker(ControlCenterClockStyle)

        //控制中心日期样式
        loadHooker(ControlCenterDateStyle)

        //通知两侧对齐
        if (prefs(ModulePrefs).getBoolean("enable_notification_align_both_sides", false)) {
            loadHooker(EnableNotificationAlignBothSides)
        }
        //移除控制中心多用户
        if (prefs(ModulePrefs).getBoolean("remove_control_center_user_switcher", false)) {
            if (SDK < A13) loadHooker(RemoveControlCenterUserSwitcher)
        }
        //控制中心底部网络警告
        loadHooker(RemoveStatusBarBottomNetworkWarn)

        //通知背景透明度
//        loadHooker(CustomNotificationBackgroundTransparency)
        if (SDK >= A14) loadHooker(NotificationBackgroundTransParency)

        //控制中心背景透明度
        loadHooker(ControlCenterBackgroundTransParency)
    }
}