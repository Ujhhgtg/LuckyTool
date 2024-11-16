package com.luckyzyx.luckytool.hook.scopes.weather

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.hasField
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.joom.paranoid.Obfuscate

@Obfuscate
object Enable15DayWeatherExpandList : YukiBaseHooker() {
    override fun onHook() {
        //Source FutureDayWeatherItem
        "com.oplus.weather.main.view.itemview.FutureDayWeatherItem".toClass().apply {
            if (hasField { name = "isAllow15DayExpand";type = BooleanType }.not()) return@apply
            constructor { param { it.contains(BooleanType) } }.hookAll {
                before {
                    args(args.indexOfFirst { it is Boolean }).setTrue()
                }
            }
        }

        //Source UIConfigManager
        "com.oplus.weather.uiconfig.UIConfigManager".toClass().apply {
            if (hasMethod { name { it.startsWith("get") && it.contains("Day15ExpandConfig") } }) {
                method {
                    name { it.startsWith("get") && it.contains("Day15ExpandConfig") }
                }.hookAll {
                    replaceToTrue()
                }
            }
        }
    }
}