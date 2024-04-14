package com.luckyzyx.luckytool.hook.scopes.settings

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.PackageInfoClass
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.formatStringAuto
import com.luckyzyx.luckytool.utils.getAppVerInfo
import com.luckyzyx.luckytool.utils.openMarketIntent
import com.luckyzyx.luckytool.utils.safeOf

object HookAppDetails : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onHook() {
        val isPackName = prefs(ModulePrefs).getBoolean("show_package_name_in_app_details", false)
        val isSdk = prefs(ModulePrefs).getBoolean("show_sdk_in_app_details", false)
        val isFirstInstallTime =
            prefs(ModulePrefs).getBoolean("show_first_install_time_in_app_details", false)
        val isLastUpdateTime =
            prefs(ModulePrefs).getBoolean("show_last_update_time_in_app_details", false)
        val isEnableCopy =
            prefs(ModulePrefs).getBoolean("enable_long_press_to_copy_in_app_details", false)
        val isIconMarket = prefs(ModulePrefs).getBoolean("click_icon_open_market_page", false)

        //Source AppInfoFeature
        "com.oplus.settings.feature.appmanager.AppInfoFeature".toClass().apply {
            method { name = "setAppLabelAndIcon";paramCount = 1 }.hook {
                after {
                    val mRootView = field { name = "mRootView" }.get(instance).cast<View>()
                        ?: return@after
                    val appButtonsPreferenceController = args().first().any() ?: return@after
                    val instrumentedPreferenceFragment =
                        appButtonsPreferenceController.current().field { name = "mFragment" }
                            .any() ?: return@after
                    val packageInfo = instrumentedPreferenceFragment.current().field {
                        type = PackageInfoClass
                    }.cast<PackageInfo>() ?: return@after
                    val appInfo = packageInfo.applicationInfo

                    val context = mRootView.context
                    context.injectModuleAppResources()

                    val appIcon = mRootView.findViewById<ImageView>(
                        context.resources.getIdentifier(
                            "app_icon", "id", this@HookAppDetails.packageName
                        )
                    )
                    val appSize = mRootView.findViewById<TextView>(
                        context.resources.getIdentifier(
                            "app_size", "id", this@HookAppDetails.packageName
                        )
                    )
                    val packName = packageInfo.packageName
                    val appVerInfo = context.getAppVerInfo(packName, false) ?: return@after
                    val version = "${appVerInfo.versionName}(${appVerInfo.versionCode})" +
                            if (appVerInfo.versionCommit.isNullOrBlank()) ""
                            else "_${appVerInfo.versionCommit}"
                    val versionText = context.getString(
                        context.resources.getIdentifier(
                            "version_text", "string", this@HookAppDetails.packageName
                        ), version
                    )

                    if (isIconMarket) appIcon?.setOnClickListener {
                        it.context.openMarketIntent(packName)
                    }


                    val list = ArrayList<String>()

                    if (isPackName) list.add(packName)

                    list.add(versionText)

                    if (isSdk) {
                        val min = appInfo.minSdkVersion
                        val target = appInfo.targetSdkVersion
                        list.add("Min $min Target $target")
                    }

                    if (isFirstInstallTime) {
                        val firstInstallTimeStr = safeOf("First Install Time") {
                            context.getString(R.string.first_install_time)
                        }
                        val firstInstallTime =
                            formatDate("YYYY/MM/dd HH:mm:ss", packageInfo.firstInstallTime)
                        list.add("$firstInstallTimeStr $firstInstallTime")
                    }
                    if (isLastUpdateTime) {
                        val lastUpdateTimeStr = safeOf("Last Update Time") {
                            context.getString(R.string.last_update_time)
                        }
                        val lastUpdateTime =
                            formatDate("YYYY/MM/dd HH:mm:ss", packageInfo.lastUpdateTime)
                        list.add("$lastUpdateTimeStr $lastUpdateTime")
                    }

                    appSize?.apply {
                        if (isEnableCopy) setTextIsSelectable(true)
                        text = formatStringAuto(list, "\n")
                    }
                }
            }
        }
    }
}