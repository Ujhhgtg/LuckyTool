package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.alarmclock.AlarmClockWidget
import com.luckyzyx.luckytool.utils.DexkitUtils

object HookAlarmClock : YukiBaseHooker() {
    override fun onHook() {

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //桌面时钟组件红一
            loadHooker(AlarmClockWidget(dexKitBridge))
        }

    }
}