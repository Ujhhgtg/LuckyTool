package com.luckyzyx.luckytool.hook.scopes.smartsidebar

import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.IntentUtils
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.startMirageWindow

@Obfuscate
object EnableRunInBackground : YukiBaseHooker() {
    private const val BackgroundRunToolCls =
        "com.oplus.smartsidebar.panelview.edgepanel.data.entrybeans.models.tools.BackgroundRunTool"

    override fun onHook() {
        val osCode = getOSVersionCode

        //Source BackgroundRunTool
        BackgroundRunToolCls.toClass().apply {
            method { name = "handle" }.hook {
                replaceUnit {
                    if (osCode >= 34) {
                        startMirageWindow(null)
                    } else {
                        val context = field { type = ContextClass;superClass() }.get(instance)
                            .cast<Context>() ?: return@replaceUnit
                        IntentUtils(context).startBackgroundRunServiceV14()
                    }
                }
            }
            method { name = "isToolAvailable" }.hook {
                replaceToTrue()
            }
        }

        //Source ToolEntryHelper
        "com.oplus.smartsidebar.panelview.edgepanel.data.entrybeans.ToolEntryHelper".toClass()
            .apply {
                method { name = "loadTools" }.hook {
                    after {
                        val context = field { type = ContextClass; superClass() }.get(instance)
                            .cast<Context>() ?: return@after
                        val tool = BackgroundRunToolCls.toClass().buildOf(context) {
                            param(ContextClass)
                        }
                        method { name = "put" }.get(instance).call(tool)
                    }
                }
            }
    }
}