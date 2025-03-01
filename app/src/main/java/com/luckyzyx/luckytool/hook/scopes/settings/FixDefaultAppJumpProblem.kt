package com.luckyzyx.luckytool.hook.scopes.settings

import android.content.Context
import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.safeOfNull

@Obfuscate
object FixDefaultAppJumpProblem : YukiBaseHooker() {
    override fun onHook() {
        //Source DefaultAppManagerPreferenceController
        "com.oplus.settings.feature.appmanager.controller.DefaultAppManagerPreferenceController".toClass()
            .apply {
                method { name = "handlePreferenceTreeClick" }.hook {
                    before {
                        val preference = args().first().any() ?: return@before
                        val key = preference.current().method { name = "getKey";superClass() }
                            .string()
                        val context = preference.current().field {
                            type = ContextClass;superClass()
                        }.cast<Context>() ?: return@before
                        if (key == "default_apps_manager") {
                            val intent = Intent("action.oplusos.safecenter.DefaultAppListActivity")
                            safeOfNull { context.startActivity(intent) }
                        }
                    }
                }
            }
    }
}