package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object ForceEnableRecentTaskMemoryDisplay : YukiBaseHooker() {
    override fun onHook() {
        //Source MemoryInfoManager
        "com.oplus.quickstep.memory.MemoryInfoManager".toClass().apply {
            method { name = "isAllowMemoryInfoDisplay" }.hook {
                replaceToTrue()
            }
            method { name = "saveAllowMemoryInfoDisplay" }.hook {
                before {
                    args().first().setTrue()
                }
            }
        }
    }
}