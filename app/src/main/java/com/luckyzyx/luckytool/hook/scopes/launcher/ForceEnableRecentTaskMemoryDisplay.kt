package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object ForceEnableRecentTaskMemoryDisplay : YukiBaseHooker() {
    override fun onHook() {
        //Source MemoryInfoManager
        "com.oplus.quickstep.memory.MemoryInfoManager".toClass().resolve().apply {
            firstMethod { name = "isAllowMemoryInfoDisplay" }.hook {
                replaceToTrue()
            }
            firstMethod { name = "needMemoryDetail" }.hook {
                replaceToTrue()
            }
        }
    }
}