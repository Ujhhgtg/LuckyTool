package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object ForceEnableRecentTaskMemoryDisplay : YukiBaseHooker() {
    override fun onHook() {
        //Source MemoryInfoManager
        "com.oplus.quickstep.memory.MemoryInfoManager".toClass().apply {
            method { name = "isAllowMemoryInfoDisplay" }.hook {
                replaceToTrue()
            }
            method { name = "needMemoryDetail" }.hook {
                replaceToTrue()
            }
        }
    }
}