package com.luckyzyx.luckytool.hook.hookers

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.hookers.global.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.scopes.smartsidebar.ForceEnableBuoyAutomaticallyHides
import com.luckyzyx.luckytool.hook.scopes.smartsidebar.HookFeatureOption
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getAppVerInfo

object HookSmartSidebar : YukiBaseHooker() {
    override fun onHook() {
        val appVer = prefs(ModulePrefs).getAppVerInfo(packageName)

        loadHooker(HookGlobalFeatureConfig)

        val v14 = appVer?.versionCode?.let { it >= 14000000 } ?: false

        //HookFeatureOption
        if (v14) loadHooker(HookFeatureOption)

        if (prefs(ModulePrefs).getBoolean("force_enable_buoy_automatically_hides", false)) {
            if (SDK == A12) loadHooker(ForceEnableBuoyAutomaticallyHides)
        }
//
//        val list = ArrayList<Any>()
//
//        "qb.b".toClass().apply {
//            method { name = "getAll" }.hook {
//                after {
//                    val res = result<List<Any>>() ?: return@after
//                    list.addAll(res)
//                    YLog.debug("getAll is call")
//                }
//            }
//        }
//
//        "com.oplus.smartsidebar.panelview.edgepanel.data.entrybeans.ToolEntryHelper".toClass()
//            .apply {
//                method { name = "loadTools" }.hook {
//                    after {
//                        YLog.debug("loadTools is call")
//
//                        val context = field {
//                            type = ContextClass;superClass()
//                        }.get(instance).cast<Context>() ?: return@after
//                        val OnlineToolCls =
//                            "com.oplus.smartsidebar.panelview.edgepanel.data.entrybeans.models.tools.OnlineTool"
//
//                        val ba = list.find {
//                            it.current().method { name = "getAliasName" }.string()
//                                .contains("scene_backstageaudio")
//                        }
//                        YLog.debug("scene_backstageaudio status -> ${ba != null}")
//
//                        if (ba != null) {
//                            val ins = OnlineToolCls.toClass().buildOf(context, ba)
//                            method { name = "put" }.get(instance).call(ins)
//                        }
//
//                    }
//                }
//            }
//
//        "com.oplus.smartsidebar.panelview.edgepanel.data.entrybeans.models.tools.OnlineTool".toClass()
//            .apply {
//                method { name = "handle" }.hook {
//                    after {
//                        YLog.debug("OnlineTool handle is after call one")
//
//                        val info = field { name = "mOnlineEntry" }.get(instance).any()
//                            ?: return@after
//                        val aliasName = info.current().method { name = "getAliasName" }.string()
//                        if (aliasName.contains("scene_backstageaudio").not()) return@after
//                        val ins = "com.oplus.smartsidebar.utils.a0".toClass().field {
//                            type { it != BooleanType }
//                        }.get().any() ?: return@after
//                        val componentName = ComponentName(
//                            "com.oplus.exsystemservice",
//                            "com.oplus.backgroundstream.RouteForegroundService"
//                        )
//                        ins.current().method {
//                            param(
//                                "com.oplus.smartsidebar.permanent.repository.database.OnlineEntryBean",
//                                ComponentNameClass
//                            )
//                        }.call(info, componentName)
//                        YLog.debug("OnlineTool handle is after call two")
//
//                    }
//                }
//                method { name = "isToolAvailable" }.hook {
//                    after {
//
//                        val info = field { name = "mOnlineEntry" }.get(instance).any()
//                            ?: return@after
//                        YLog.debug("OnlineEntryBean -> ${info.toString()}")
//
//                        val aliasName = info.current().method { name = "getAliasName" }.string()
//                        if (aliasName.contains("scene_backstageaudio").not()) return@after
//                        YLog.debug("OnlineTool isToolAvailable -> $result")
//
//                        resultTrue()
//                    }
//                }
//            }
//
//        "com.oplus.smartsidebar.permanent.repository.database.OnlineEntryBean".toClass().apply {
//            method { name = "getBusinessPkgVersionLimitMax" }.hook {
//                after {
//                    val aliasName = method { name = "getAliasName" }.get(instance).string()
//                    if (aliasName.contains("scene_backstageaudio")) {
////                        YLog.debug("OnlineEntryBean -> ${instance.toString()}")
//                        resultNull()
//                    }
//                }
//            }
//        }


//        //Source OnlineEntryBean
//        "com.oplus.smartsidebar.permanent.repository.database.OnlineEntryBean".toClass().apply {
//            constructor { param { it.first() == LongClass && it.last() == MapClass } }.hook {
//                after {
//                    val aliasName = method { name = "getAliasName" }.get(instance).string()
//                    if (aliasName.contains("scene_backstageaudio")) {
//                        YLog.debug(instance.toString())
//
//                        field {
//                            name = "businessPkgVersionLimitMax"
//                        }.get(instance).set("20000")
//                    }
//                }
//            }
//        }

//        OnlineEntryBean(
//        id=16, name=听剧模式, language=zh_CN,
//        nameTranslated=听剧模式,
//        aliasName=scene_backstageaudio,
//        functionType=1,
//        pictureLink="https://sidebar-iconfs-cn.allawnfs.com/sidebar-admin//pictures/13-听剧模式.png",
//        actionLink=action#service#oplus.intent.action.BACKGROUND_STREAM_SERVICE,
//        actionExtraMap={},
//        recommend=null, recommendOrder=null,
//        business=听剧模式,
//        businessPkgName=com.oplus.exsystemservice,
//        businessPkgVersionLimit=0,
//        businessPkgVersionLimitMax=10018,
//        businessMetaData=, extra=, availableBusinessPkgName=com.oplus.exsystemservice,
//        screenSplitActionLink=##, screenSplitActionExtraMap={})

//
//        "com.oplus.smartsidebar.utils.a0\$a".toClass().apply {
//            method {
//                param(
//                    "com.oplus.smartsidebar.permanent.repository.database.OnlineEntryBean"
//                )
//                returnType = BooleanType
//            }.hook {
//                before {
//                    val bean = args().first().any() ?: return@before
//                    val aliasName = bean.current().method { name = "getAliasName" }.string()
//                    if (aliasName.contains("scene_backstageaudio")) {
//
//                        val businessPkgName = bean.current().field {
//                            name = "businessPkgName"
//                        }.string()
//                        bean.current().field {
//                            name = "availableBusinessPkgName"
//                        }.set(businessPkgName)
//
//                        resultTrue()
//                    }
//                }
//            }
//        }
//
//        "com.oplus.smartsidebar.panelview.edgepanel.data.viewdatahandlers.AllAppDataHandlerImpl".toClass()
//            .apply {
//                method { name = "onAppItemClicked" }.hook {
//                    before {
//                        val data = args().first().any() ?: return@before
//                        val bool = args().last().boolean()
//
//                        val bean = data.current().method { name = "getEntryBean" }.call()
//                        YLog.debug("AllAppDataHandlerImpl($bool) ${bean.toString()}")
//
//                    }
//                }
//            }
//
//        "com.oplus.smartsidebar.panelview.edgepanel.data.viewdatahandlers.UserListDataHandlerImpl".toClass()
//            .apply {
//                method { name = "onItemClick" }.hook {
//                    before {
//                        val data = args().first().any() ?: return@before
//                        val bool = args().last().boolean()
//
//                        val bean = data.current().method { name = "getEntryBean" }.call()
//                        YLog.debug("UserListDataHandlerImpl($bool) ${bean.toString()}")
//
//                    }
//                }
//            }

    }
}