package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.alarmclock.AlarmClockWidget

object HookAlarmClock : YukiBaseHooker() {
    override fun onHook() {
        //桌面时钟组件红一
        loadHooker(AlarmClockWidget)
    }
}