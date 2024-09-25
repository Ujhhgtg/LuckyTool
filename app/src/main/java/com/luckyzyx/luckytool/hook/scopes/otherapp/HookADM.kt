package com.luckyzyx.luckytool.hook.scopes.otherapp

import android.app.Activity
import androidx.preference.PreferenceManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookADM : YukiBaseHooker() {
    override fun onHook() {

        if (prefs(ModulePrefs).getBoolean("adm_unlock_pro", false)) {
            loadHooker(UnlockAdmPro)
        }
    }

    object UnlockAdmPro : YukiBaseHooker() {
        override fun onHook() {
            //Source Main
            "com.dv.get.Main".toClass().apply {
                method { name = "onCreate" }.hook {
                    after {
                        val activity = instance<Activity>()
                        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                        sp.edit().putBoolean("hua_voices", false).commit()
                    }
                }
            }
        }
    }
}