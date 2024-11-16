package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.scopes.launcher.AllowLockingUnLockingOfExcludedActivity
import com.luckyzyx.luckytool.hook.scopes.launcher.ForceEnableRecentTaskMemoryDisplay
import com.luckyzyx.luckytool.hook.scopes.launcher.HookAppBadge
import com.luckyzyx.luckytool.hook.scopes.launcher.HookDeviceProfileOption
import com.luckyzyx.luckytool.hook.scopes.launcher.HookLauncherFeature
import com.luckyzyx.luckytool.hook.scopes.launcher.LauncherLayoutRowColume
import com.luckyzyx.luckytool.hook.scopes.launcher.LongPressAppIconOpenAppDetails
import com.luckyzyx.luckytool.hook.scopes.launcher.PageIndicator
import com.luckyzyx.luckytool.hook.scopes.launcher.RecentTaskListClearButton
import com.luckyzyx.luckytool.hook.scopes.launcher.RemoveAppUpdateGreenDot
import com.luckyzyx.luckytool.hook.scopes.launcher.RemoveBottomAppIconOfRecentTaskList
import com.luckyzyx.luckytool.hook.scopes.launcher.RemoveFolderPreviewBackground
import com.luckyzyx.luckytool.hook.scopes.launcher.UnlockTaskLocks
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object HookLauncher : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        loadHooker(HookGlobalFeatureConfig)

        //HookLauncherFeature
        loadHooker(HookLauncherFeature)

        //HookDeviceProfileOption
        loadHooker(HookDeviceProfileOption)

        //分页组件
        loadHooker(PageIndicator)

        //堆叠布局
//        loadHooker(StackedTaskLayout)

        //应用徽章
        if (SDK >= A13) loadHooker(HookAppBadge)

        //设置桌面布局行列数
        if (prefs(ModulePrefs).getBoolean("launcher_layout_enable", false)) {
            loadHooker(LauncherLayoutRowColume)
        }
        //移除文件夹预览背景
        if (prefs(ModulePrefs).getBoolean("remove_folder_preview_background", false)) {
            loadHooker(RemoveFolderPreviewBackground)
        }
        //最近任务列表清除按钮
        if (prefs(ModulePrefs).getBoolean("remove_recent_task_list_clear_button", false)) {
            loadHooker(RecentTaskListClearButton)
        }
        //最近任务列表长按APP图标打开应用详情
        if (prefs(ModulePrefs).getBoolean("long_press_app_icon_open_app_details", false)) {
            loadHooker(LongPressAppIconOpenAppDetails)
        }
        //移除最近任务列表底部APP图标
        if (prefs(ModulePrefs).getBoolean("remove_bottom_app_icon_of_recent_task_list", false)) {
            loadHooker(RemoveBottomAppIconOfRecentTaskList)
        }
        //解锁后台任务锁定限制
        if (prefs(ModulePrefs).getBoolean("unlock_task_locks", false)) {
            loadHooker(UnlockTaskLocks)
        }
        //允许锁定或解锁已排除活动
        if (prefs(ModulePrefs).getBoolean("allow_locking_unlocking_of_excluded_activity", false)) {
            loadHooker(AllowLockingUnLockingOfExcludedActivity)
        }
        //移除App更新圆点
        if (osCode >= 33) loadHooker(RemoveAppUpdateGreenDot)

        //强制启用最近任务内存显示
        if (prefs(ModulePrefs).getBoolean("force_enable_recent_task_memory_display", false)) {
            loadHooker(ForceEnableRecentTaskMemoryDisplay)
        }

        //com.android.quickstep.views.OplusTaskMenuViewImpl
        //res/layout/oplus_task_menu_option.xml

        //<string name="oplus_shortcut_lock_app">锁定</string>
        //<string name="oplus_shortcut_locked_app">解锁</string>
        //<string name="oplus_rapid_reach_float_window">浮窗</string>
        //<string name="recent_task_option_split_screen">分屏</string>
        //<string name="oplus_privacy_not_show_preview">隐藏内容</string>
        //<string name="oplus_privacy_show_preview">显示内容</string>
        //<string name="oplus_shortcut_lock_setting">管理</string>

        //com.android.launcher3.popup.OplusBaseSystemShortcut
        //OplusAppInfo etc. -> Click

        //AppEdit
        //com.oplus.uxicon.ui.ui.UxEditPanelFragment -> res/layout/edit_panel_layout.xml
        //<string name="no_icon_pack_toast">没有支持替换图标的图标包</string>
    }
}