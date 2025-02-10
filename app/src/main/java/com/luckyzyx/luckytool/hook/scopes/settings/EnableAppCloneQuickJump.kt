package com.luckyzyx.luckytool.hook.scopes.settings

import android.content.Context
import android.content.pm.PackageInfo
import android.view.Menu
import android.view.MenuItem
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.PackageInfoClass
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.isSystem

@Obfuscate
object EnableAppCloneQuickJump : YukiBaseHooker() {
    override fun onHook() {
        //Source AppInfoDashboardFragment
        "com.android.settings.applications.appinfo.AppInfoDashboardFragment".toClass().apply {
            method { name = "onCreateOptionsMenu" }.hook {
                after {
                    val menu = args().first().cast<Menu>() ?: return@after
                    val context = method { name = "getContext";superClass() }.get(instance)
                        .invoke<Context>() ?: return@after
                    val packageInfo = field { type = PackageInfoClass }.get(instance)
                        .cast<PackageInfo>() ?: return@after
                    if (packageInfo.isSystem()) return@after
                    val label = AppUtils(context).getAppLabel("com.oplus.multiapp")
//                    val menuInflater = args().last().cast<MenuInflater>() ?: return@after
                    menu.add(0, 999, 0, label)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                }
            }
            method { name = "onOptionsItemSelected" }.hook {
                before {
                    val menuItem = args().first().cast<MenuItem>() ?: return@before
                    if (menuItem.itemId == 999) {
                        val context = method { name = "getContext";superClass() }.get(instance)
                            .invoke<Context>() ?: return@before
                        val packageInfo = field { type = PackageInfoClass }.get(instance)
                            .cast<PackageInfo>() ?: return@before
                        val packName = packageInfo.packageName
                        val label = AppUtils(context).getAppLabel(packName)
                        try {
                            AppUtils(context).openMultiAppIntent(label, packName)
                        } catch (e: Throwable) {
                            YLog.debug("EnableAppCloneQuickJump startActivity error", e)
                        }
                    }
                }
            }
        }
    }
}