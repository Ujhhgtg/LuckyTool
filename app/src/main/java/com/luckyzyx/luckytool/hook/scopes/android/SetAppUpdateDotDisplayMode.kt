package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.luckyzyx.luckytool.hook.utils.OplusCommonFeatureUtils
import com.luckyzyx.luckytool.utils.ModulePrefs

object SetAppUpdateDotDisplayMode : YukiBaseHooker() {

    private const val IOplusPkgStartInfoManager = "com.android.server.pm.IOplusPkgStartInfoManager"

    override fun onHook() {
        val mode = prefs(ModulePrefs).getString("set_app_update_dot_display_mode", "0")
        if (mode == "0") return

        //Source PackageManagerServiceExtImpl
        "com.android.server.pm.PackageManagerServiceExtImpl".toClass().apply {
            method { name = "handleSuccessAtEndInHPPI";paramCount = 6 }.hook {
                after {
                    val packName = args(2).string()
                    val installerPackageName = args(3).string()
                    val isUpdate = args(4).boolean()
                    val marketList = field {
                        name = "DEFAULT_MARKET_LIST";type = ListClass
                    }.get().cast<List<String>>() ?: java.util.ArrayList()
                    if (marketList.contains(installerPackageName) || isUpdate) {
                        OplusCommonFeatureUtils(classLoader).apply {
                            val defaultCommonFeature = getDefaultFeature(IOplusPkgStartInfoManager)
                                ?: return@after
                            val manager = getFeatureCache(defaultCommonFeature) ?: return@after
                            if (mode == "1") manager.current().method {
                                name = "addPkgToNotLaunchedList"
                            }.call(packName)
                        }
                    }
                }
            }
        }
    }
}