package com.luckyzyx.luckytool.hook.scopes.smartsidebar

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object ForceEnableBuoyAutomaticallyHides : YukiBaseHooker() {
    override fun onHook() {
        //Source EdgePanelUtils
        "com.coloros.edgepanel.utils.EdgePanelUtils".toClass().resolve().apply {
            firstMethodOrNull { name = "isMetaDataSupportByPackage";parameterCount = 2 }?.hook {
                after {
                    val packName = args().first().string()
                    val key = args().last().string()
                    if (packName == "com.android.systemui" && key == "sidebar_gesture_support") resultTrue()
                }
            }
        }
    }
}