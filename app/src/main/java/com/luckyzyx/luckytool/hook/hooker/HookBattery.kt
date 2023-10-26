package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.battery.BatteryFeatureProvider
import com.luckyzyx.luckytool.hook.scope.battery.DisplayModuleCalculatesBatteryHealthData
import com.luckyzyx.luckytool.hook.scope.battery.HookBatteryNotify
import com.luckyzyx.luckytool.hook.scope.battery.LauncherHighTempreatureProtection
import com.luckyzyx.luckytool.hook.scope.battery.RemoveBatteryTemperatureControl
import com.luckyzyx.luckytool.hook.scope.battery.UnlockStartupLimit
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object HookBattery : YukiBaseHooker() {
    override fun onHook() {
        if (SDK < A13) try {
            DexkitUtils.create(appInfo.sourceDir)?.close()
        } catch (e: UnsatisfiedLinkError) {
            return
        }

        //BatteryFeatureProvider
        loadHooker(BatteryFeatureProvider)

        //电池通知
        loadHooker(HookBatteryNotify)

        //移除自启数量限制
        if (prefs(ModulePrefs).getBoolean("unlock_startup_limit", false)) {
            if (SDK >= A13) loadHooker(UnlockStartupLimit)
        }

        //移除电池温度控制
        if (prefs(ModulePrefs).getBoolean("remove_battery_temperature_control", false)) {
            loadHooker(RemoveBatteryTemperatureControl)
            loadHooker(LauncherHighTempreatureProtection)
        }

        //显示模块计算电池健康数据
        if (prefs(ModulePrefs).getBoolean("display_module_calculates_battery_health_data", false)) {
            if (SDK >= A13) loadHooker(DisplayModuleCalculatesBatteryHealthData)
        }

    }
}