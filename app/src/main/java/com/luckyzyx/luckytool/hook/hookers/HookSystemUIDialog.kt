package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.AutoTapStartRecordingOrCastingDialog
import com.luckyzyx.luckytool.hook.scopes.systemui.DisableDuplicateFloatingWindow
import com.luckyzyx.luckytool.hook.scopes.systemui.DisableHeadphoneHighVolumeWarning
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveLowBatteryDialogWarning
import com.luckyzyx.luckytool.hook.scopes.systemui.RemoveUSBConnectDialog
import com.luckyzyx.luckytool.hook.scopes.systemui.RunFloatingWindowTasksInForeground
import com.luckyzyx.luckytool.hook.scopes.systemui.VolumeDialogBackground
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookSystemUIDialog : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->

            //音量对话框背景透明度
            loadHooker(VolumeDialogBackground(dexKitBridge))

        }

        //禁用复制悬浮窗
        if (prefs(ModulePrefs).getBoolean("disable_duplicate_floating_window", false)) {
            loadHooker(DisableDuplicateFloatingWindow)
        }
        //禁用耳机高音量警告
        if (prefs(ModulePrefs).getBoolean("disable_headphone_high_volume_warning", false)) {
            loadHooker(DisableHeadphoneHighVolumeWarning)
        }
        //移除低电量对话框警告
        if (prefs(ModulePrefs).getBoolean("remove_low_battery_dialog_warning", false)) {
            loadHooker(RemoveLowBatteryDialogWarning)
        }
        //移除USB连接对话框
        if (prefs(ModulePrefs).getBoolean("remove_usb_connect_dialog", false)) {
            loadHooker(RemoveUSBConnectDialog)
        }
        //自动点击开始录制或投射对话框
        if (prefs(ModulePrefs).getBoolean("auto_tap_start_recording_or_casting_dialog", false)) {
            loadHooker(AutoTapStartRecordingOrCastingDialog)
        }
        //浮窗贴边前台运行
        if (prefs(ModulePrefs).getBoolean("run_floating_window_tasks_in_foreground", false)) {
            if (osCode in 26..33) loadHooker(RunFloatingWindowTasksInForeground)
        }
    }
}