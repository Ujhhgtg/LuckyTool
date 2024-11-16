package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object ReplaceSystemRootStateDetection : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        val isEnable = prefs(ModulePrefs).getBoolean("replace_system_root_state_detection", false)
        if (SDK < A12 || !isEnable) return

        //Source HeimdallService
        if (osCode > 26) "com.android.server.oplus.heimdall.HeimdallService".toClass().apply {
            method { name = "isRootEnable" }.hook {
                replaceToFalse()
            }
        }

        //Source RootService
        "com.android.server.oplus.heimdall.service.RootService".toClass().apply {
            method { name = "isRoot" }.hook {
                replaceToFalse()
            }
        }

        //Source HeimdallService
        "com.android.server.oplus.heimdall.root.RootDetector".toClass().apply {
            method { name = "checkDeviceRootStatus" }.hook {
                intercept()
            }
        }
    }
}