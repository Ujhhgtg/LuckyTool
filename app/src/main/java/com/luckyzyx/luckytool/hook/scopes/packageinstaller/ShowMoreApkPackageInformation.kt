package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.safeOf
import com.luckyzyx.luckytool.utils.safeOfNull

@Obfuscate
object ShowMoreApkPackageInformation : YukiBaseHooker() {

    private lateinit var apkInfo: Any
    private lateinit var sourceInfo: Any

    override fun onHook() {
        //Source ApkInfoView
        "com.android.packageinstaller.oplus.view.ApkInfoView".toClass().apply {
            method { name = "loadApkInfo" }.hook {
                after {
                    val apkInfoView = instance<LinearLayout>()
                    val context = apkInfoView.context
                    val pm = context.packageManager

//                    apkInfoView.allViews.forEachIndexed { index, view ->
//                        val ids = safeOfNull { context.resources.getResourceEntryName(view.id) }
//                        YLog.debug("$index -> ${view.javaClass} | $ids")
//                    }

                    apkInfo = args().first().any() ?: return@after
                    sourceInfo = args().last().any() ?: return@after
                    val actionType = sourceInfo.current().field { name = "actionType" }.int()
                    val installSource = sourceInfo.current().field { name = "sourceName" }.string()

                    val packName = apkInfo.current().field { name = "packageName" }.string()
                    val versionName = apkInfo.current().field { name = "versionName" }.string()
                    val versionCode = apkInfo.current().field { name = "versionCode" }.int()
                    val apkFilePath = apkInfo.current().field { name = "apkPath" }.string()

                    val packInfo = PackageUtils(pm).getPackageArchiveInfo(apkFilePath, 1)
                    val newMin = packInfo?.applicationInfo?.minSdkVersion
                    val newTarget = packInfo?.applicationInfo?.targetSdkVersion

                    val curPackInfo = PackageUtils(pm).getPackageInfo(packName, 0)
                    val curVersionName = curPackInfo?.versionName
                    val curVersionCode = curPackInfo?.longVersionCode
                    val curMin = curPackInfo?.applicationInfo?.minSdkVersion
                    val curTarget = curPackInfo?.applicationInfo?.targetSdkVersion

                    val isInstalled = curPackInfo != null
                    val isInstall = actionType == 0

                    @Suppress("UNUSED_VARIABLE")
                    val isUninstall = actionType == 1

                    val newApkHeaderView = LinearLayout(context).apply {
                        val newLayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(16.dp, 8.dp, 16.dp, 8.dp)
                        }
                        layoutParams = newLayoutParams
                        orientation = LinearLayout.HORIZONTAL
                    }

                    val mApkIcon =
                        field { name = "mAppIcon" }.get(instance).cast<ImageView>()?.apply {
                            safeOfNull { parent as ViewGroup }?.removeView(this)
                        } ?: return@after
                    newApkHeaderView.addView(mApkIcon)

                    val newApkNameView = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                    }
                    val mApkName =
                        field { name = "mAppName" }.get(instance).cast<TextView>()?.apply {
                            safeOfNull { parent as ViewGroup }?.removeView(this)
                        } ?: return@after
                    newApkNameView.addView(mApkName)

                    val mApkPackName = TextView(context).apply {
                        val newLayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(mApkName.marginLeft, 8.dp, 16.dp, marginBottom)
                        }
                        layoutParams = newLayoutParams
                        text = packName
                        setTextIsSelectable(true)
                    }
                    newApkNameView.addView(mApkPackName)

                    newApkHeaderView.addView(newApkNameView)

                    val mApkVersion = TextView(context).apply {
                        val newLayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(20.dp, 8.dp, 20.dp, 8.dp)
                        }
                        layoutParams = newLayoutParams
                        text = if (isInstalled)
                            """
                                ${getApkVersionText(context)}
                                $curVersionName($curVersionCode) → $versionName($versionCode)
                            """.trimIndent()
                        else
                            """
                                ${getApkVersionText(context)}
                                $versionName($versionCode)
                            """.trimIndent()
                        setTextIsSelectable(true)
                    }

                    val mApkSdk = TextView(context).apply {
                        val newLayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(20.dp, 8.dp, 20.dp, 8.dp)
                        }
                        layoutParams = newLayoutParams
                        text = if (isInstalled)
                            """
                                SDK: 
                                Min SDK: $curMin → $newMin  |   Target SDK: $curTarget → $newTarget
                            """.trimIndent()
                        else
                            """
                                SDK: 
                                Min SDK: $newMin  |   Target SDK: $newTarget
                            """.trimIndent()
                        setTextIsSelectable(true)
                    }

                    val mApkInstallSource = TextView(context).apply {
                        val newLayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(20.dp, 8.dp, 20.dp, 8.dp)
                        }
                        layoutParams = newLayoutParams
                        text = getInstallSourceText(context, installSource)
                        setTextIsSelectable(true)
                    }

                    apkInfoView.removeAllViews()

                    apkInfoView.addView(newApkHeaderView)
                    apkInfoView.addView(mApkVersion)
                    if (isInstall) apkInfoView.addView(mApkSdk)
                    if (isInstall) apkInfoView.addView(mApkInstallSource)

                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getApkVersionText(context: Context): String {
        return safeOf("Version: ") {
            context.resources.getString(
                context.resources.getIdentifier(
                    "app_info_version", "string", context.packageName
                )
            )
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getInstallSourceText(context: Context, source: String): String {
        return safeOf("From: $source") {
            context.resources.getString(
                context.resources.getIdentifier(
                    "from_source", "string", context.packageName
                ), source
            )
        }
    }

}