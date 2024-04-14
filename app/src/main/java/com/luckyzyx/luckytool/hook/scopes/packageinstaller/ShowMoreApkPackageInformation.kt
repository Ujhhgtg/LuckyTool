package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.formatStringAuto
import com.luckyzyx.luckytool.utils.safeOf

object ShowMoreApkPackageInformation : YukiBaseHooker() {

    private lateinit var apkInfo: Any
    private lateinit var sourceInfo: Any

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        //Source ApkInfoView
        "com.android.packageinstaller.oplus.view.ApkInfoView".toClass().apply {
            method { name = "loadApkInfo" }.hook {
                after {
                    val apkInfoView = instance<View>()
                    val context = apkInfoView.context
                    val pm = context.packageManager

                    val mAppVersion = field { name = "mAppVersion" }.get(instance).cast<TextView>()
                        ?: return@after
                    mAppVersion.apply {
                        (parent as LinearLayout).orientation = LinearLayout.VERTICAL
                        (layoutParams as LinearLayout.LayoutParams).width =
                            LinearLayout.LayoutParams.MATCH_PARENT
                        isSingleLine = false
                        setTextIsSelectable(true)
                    }

                    apkInfo = args().first().any() ?: return@after
                    sourceInfo = args().last().any() ?: return@after
                    val actionType = getActionType()

                    val packName = getPackName()
                    val versionName = getVersionName()
                    val versionCode = getVersionCode()
                    val apkFilePath = getApkFilePath()

                    val packInfo = PackageUtils(pm).getPackageArchiveInfo(apkFilePath, 1)
                    val min = packInfo?.applicationInfo?.minSdkVersion
                    val target = packInfo?.applicationInfo?.targetSdkVersion

                    val curPackInfo = PackageUtils(pm).getPackageInfo(packName, 0)
                    val curVersionName = curPackInfo?.versionName
                    val curVersionCode = curPackInfo?.longVersionCode
                    val curMin = curPackInfo?.applicationInfo?.minSdkVersion
                    val curTarget = curPackInfo?.applicationInfo?.targetSdkVersion

                    val isInstalled = curPackInfo != null

                    val isInstall = actionType == 0
                    val isUninstall = actionType == 1

                    context.injectModuleAppResources()
                    val currentVersionStr = safeOf("Current Version") {
                        context.getString(R.string.show_more_apk_package_information_current_version)
                    }
                    val iterativeVersionStr = safeOf("Iterative Version") {
                        context.getString(R.string.show_more_apk_package_information_iterative_version)
                    }
                    val versionStr = safeOf("Version: ") {
                        context.resources.getString(
                            context.resources.getIdentifier(
                                "app_info_version", "string", context.packageName
                            )
                        )
                    }

                    val list = ArrayList<String>().apply {
                        if (isInstall) {
                            if (isInstalled) {
                                add(currentVersionStr)
                                add(packName)
                                add("$versionStr$curVersionName($curVersionCode)")
                                add("Min $curMin Target $curTarget")
                                add("")
                                add(iterativeVersionStr)
                                add("$versionStr$versionName($versionCode)")
                                add("Min $min Target $target")
                            } else {
                                add(packName)
                                add("$versionStr$versionName($versionCode)")
                                add("Min $min Target $target")
                            }
                        } else if (isUninstall) {
                            add(packName)
                            add("$versionStr$versionName($versionCode)")
                        }
                    }

                    if (list.isNotEmpty()) mAppVersion.text = formatStringAuto(list, "\n")
                }
            }
        }
    }

    private fun getActionType(): Int {
        return sourceInfo.current().field { name = "actionType" }.int()
    }

    private fun getPackName(): String {
        return apkInfo.current().field { name = "packageName" }.string()
    }

    private fun getVersionName(): String {
        return apkInfo.current().field { name = "versionName" }.string()
    }

    private fun getVersionCode(): Int {
        return apkInfo.current().field { name = "versionCode" }.int()
    }

    private fun getApkFilePath(): String {
        return apkInfo.current().field { name = "apkPath" }.string()
    }
}