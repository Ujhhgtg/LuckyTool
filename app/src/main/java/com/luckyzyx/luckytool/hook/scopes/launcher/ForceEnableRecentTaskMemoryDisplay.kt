package com.luckyzyx.luckytool.hook.scopes.launcher

import android.content.Context
import android.provider.Settings
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.joom.paranoid.Obfuscate

@Obfuscate
object ForceEnableRecentTaskMemoryDisplay : YukiBaseHooker() {
    override fun onHook() {
        //Source MemoryInfoManager
        "com.oplus.quickstep.memory.MemoryInfoManager".toClass().apply {
            constructor { param(ContextClass) }.hook {
                before {
                    val context = args().first().cast<Context>() ?: return@before
                    Settings.Secure.putInt(
                        context.contentResolver, "display_memory_information_recent_task", 1
                    )
                    Settings.Secure.putInt(context.contentResolver, "allow_memory_info_display", 1)
                }
                after {
                    field { name = "mAllowMemoryInfoDisplay" }.get(instance).setTrue()
                }
            }
        }
    }
}