package com.luckyzyx.luckytool.hook.scope.calendar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object RemoveAlmanacPageInformationFlow : YukiBaseHooker() {
    override fun onHook() {
        //Source AlmanacPagesAdapter -> H5InterfaceHelper getAlmanacUrl
        "com.android.calendar.module.subscription.almanac.adapter.AlmanacPagesAdapter".toClass()
            .apply {
                method { name = "onCreateViewHolder" }.hook {
                    before {
                        args().last().set(0)
                    }
                }
            }
    }
}