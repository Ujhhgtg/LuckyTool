package com.luckyzyx.luckytool.hook.scopes.smartsidebar

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object ForceEnableBuoyAutomaticallyHides : YukiBaseHooker() {
    override fun onHook() {
        //Source EdgePanelUtils
        "com.coloros.edgepanel.utils.EdgePanelUtils".toClass().apply {
            val hasMetaData = hasMethod { name = "isMetaDataSupportByPackage";paramCount = 2 }
            if (hasMetaData) {
                method { name = "isMetaDataSupportByPackage";paramCount = 2 }.hook {
                    after {
                        val packName = args().first().string()
                        val key = args().last().string()
                        if (packName == "com.android.systemui" && key == "sidebar_gesture_support") resultTrue()
                    }
                }
            }
        }
    }
}