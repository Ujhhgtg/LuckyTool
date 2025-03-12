package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookGMSRestrict : YukiBaseHooker() {
    override fun onHook() {
        val isEnable = prefs(ModulePrefs).getBoolean("remove_gms_usage_restrictions", false)
        if (!isEnable) return

        //Source OplusBgSceneManager -> google_restric_info
        VariousClass(
            "com.android.server.am.OplusHansManager\$HansConfig", //C12 C13
            "com.android.server.hans.scene.OplusBgSceneManager" //C14 C15
        ).toClass().apply {
            method { name = "setGmsRestricted" }.hook {
                before {
                    args().first().setFalse()
                }
            }
            method { name = "isGmsRestricted" }.hook {
                replaceToFalse()
            }
        }

        //Source OplusAppStartupManager -> OplusStartupStrategy -> google_restric_info
        "com.android.server.am.OplusAppStartupManager\$OplusStartupStrategy".toClass().apply {
            method { name = "isGoogleRestricInfoOn" }.hook {
                replaceToFalse()
            }
        }
    }
}