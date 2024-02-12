package com.luckyzyx.luckytool.hook.scopes.smartsidebar

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass

object EnableRunInBackground : YukiBaseHooker() {
    private const val BackgroundRunToolCls =
        "com.oplus.smartsidebar.panelview.edgepanel.data.entrybeans.models.tools.BackgroundRunTool"

    override fun onHook() {
        //Source BackgroundRunTool
        BackgroundRunToolCls.toClass().apply {
            method { name = "handle" }.hook {
                replaceUnit {
                    val context = field { type = ContextClass;superClass() }.get(instance)
                        .cast<Context>() ?: return@replaceUnit
                    val intent = Intent("oplus.intent.action.BACKGROUND_STREAM_SERVICE").apply {
                        setPackage("com.oplus.exsystemservice")
                        component = ComponentName(
                            "com.oplus.exsystemservice",
                            "com.oplus.backgroundstream.RouteForegroundService"
                        )
                    }
                    context.startForegroundService(intent)
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