package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs

@Obfuscate
object SetAppUpdateDotDisplayMode : YukiBaseHooker() {

    private const val InstallSource = "com.android.server.pm.InstallSource"
    private const val OplusPMHelper = "com.android.server.pm.OplusOsPackageManagerHelper"

    override fun onHook() {
        val mode = prefs(ModulePrefs).getString("set_app_update_dot_display_mode", "0")
        if (mode == "0") return

        //Source PackageManagerServiceExtImpl
        "com.android.server.pm.PackageManagerServiceExtImpl".toClass().apply {
            method { name = "handleSuccessAtEndInHPPI";paramCount = 6 }.hook {
                after {
                    val packName = args(2).string()
                    val installSource = args(3).any()

                    val isUpdate = args(4).boolean()
                    val marketList = field {
                        name = "DEFAULT_MARKET_LIST";type = ListClass
                    }.get().cast<List<String>>() ?: java.util.ArrayList()

                    val installerPackageName = if (installSource is String) installSource
                    else installSource?.current()?.field { name = "mInstallerPackageName" }
                        ?.string()

                    if (isUpdate || marketList.contains(installerPackageName)) {
                        if (mode == "1") {
                            OplusPMHelper.toClass().method {
                                name = "addPkgToNotLaunchedList";param(StringClass)
                            }.get().call(packName)
                        }
                    }
                }
            }
        }
    }
}