package com.luckyzyx.luckytool.hook.hooker

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scope.calendar.RemoveAlmanacPageInformationFlow
import com.luckyzyx.luckytool.hook.scope.calendar.RemoveHolidayPageInformationFlow
import com.luckyzyx.luckytool.hook.scope.calendar.RemoveHoroscopePageInformationFlow
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

object HookCalendar : YukiBaseHooker() {
    override fun onHook() {
        if (SDK < A13) return
        //移除节假日页面信息流
        if (prefs(ModulePrefs).getBoolean("remove_holiday_page_information_flow", false)) {
            loadHooker(RemoveHolidayPageInformationFlow)
        }
        //移除黄历页面信息流
        if (prefs(ModulePrefs).getBoolean("remove_almanac_page_information_flow", false)) {
            loadHooker(RemoveAlmanacPageInformationFlow)
        }
        //移除星座页面信息流
        if (prefs(ModulePrefs).getBoolean("remove_horoscope_page_information_flow", false)) {
            loadHooker(RemoveHoroscopePageInformationFlow)
        }
    }
}