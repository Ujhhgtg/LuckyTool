package com.luckyzyx.luckytool.hook.scopes.smartsidebar

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.createInstance
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.IntentUtils
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.startMirageWindow
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object EnableRunInBackground : YukiBaseHooker() {
    private const val BackgroundRunToolCls =
        "com.oplus.smartsidebar.panelview.edgepanel.data.entrybeans.models.tools.BackgroundRunTool"

    override fun onHook() {
        val osCode = getOSVersionCode

        //Source BackgroundRunTool
        BackgroundRunToolCls.toClass().resolve().apply {
            firstMethod { name = "handle" }.hook {
                before {
                    if (osCode >= 34) {
                        startMirageWindow(null)
                    } else {
                        val context = firstField { type = Context::class;superclass() }.of(instance)
                            .get<Context>() ?: return@before
                        IntentUtils(context).startBackgroundRunServiceV14()
                    }
                    resultNull()
                }
            }
            firstMethod { name = "isToolAvailable" }.hook {
                replaceToTrue()
            }
        }

        //Source ToolEntryHelper
        "com.oplus.smartsidebar.panelview.edgepanel.data.entrybeans.ToolEntryHelper".toClass()
            .resolve().apply {
                firstMethod { name = "loadTools" }.hook {
                    after {
                        val context =
                            firstField { type = Context::class; superclass() }.of(instance)
                                .get<Context>() ?: return@after
                        val tool = BackgroundRunToolCls.toClass().createInstance(context)
                        firstMethod { name = "put" }.of(instance).invoke(tool)
                    }
                }
            }
    }
}