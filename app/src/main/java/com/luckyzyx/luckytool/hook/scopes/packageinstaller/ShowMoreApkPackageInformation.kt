package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.safeOf
import com.luckyzyx.luckytool.utils.safeOfNull
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object ShowMoreApkPackageInformation : YukiBaseHooker() {

    private lateinit var apkInfo: Any
    private lateinit var sourceInfo: Any

    override fun onHook() {
        //Source ApkInfoView
        "com.android.packageinstaller.oplus.view.ApkInfoView".toClass().resolve().apply {
            firstMethod { name = "loadApkInfo" }.hook {
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
                    val actionType =
                        sourceInfo.asResolver().firstField { name = "actionType" }.get<Int>() ?: -1
                    val installSource =
                        sourceInfo.asResolver().firstField { name = "sourceName" }.get<String>() ?: ""

                    val packName =
                        apkInfo.asResolver().firstField { name = "packageName" }.get<String>() ?: ""
                    val versionName =
                        apkInfo.asResolver().firstField { name = "versionName" }.get<String>() ?: ""
                    val versionCode =
                        apkInfo.asResolver().firstField { name = "versionCode" }.get<Int>() ?: -1
                    val apkFilePath =
                        apkInfo.asResolver().firstField { name = "apkPath" }.get<String>() ?: ""

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
                        firstField { name = "mAppIcon" }.of(instance).get<ImageView>()?.apply {
                            safeOfNull { parent as ViewGroup }?.removeView(this)
                        } ?: return@after
                    newApkHeaderView.addView(mApkIcon)

                    val newApkNameView = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                    }
                    val mApkName =
                        firstField { name = "mAppName" }.of(instance).get<TextView>()?.apply {
                            safeOfNull { parent as ViewGroup }?.removeView(this)
                        } ?: return@after
                    mApkName.textSize = 18F
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

                    val mAppSize =
                        firstField { name = "mAppSize" }.of(instance).get<TextView>()?.apply {
                            safeOfNull { parent as ViewGroup }?.removeView(this)
                        } ?: return@after
                    mAppSize.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(mApkName.marginLeft, 8.dp, 16.dp, mAppSize.marginBottom)
                    }
                    newApkNameView.addView(mAppSize)

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
                            if (isUninstall) {
                                """
                                ${getApkVersionText(context)}
                                $versionName($versionCode)
                            """.trimIndent()
                            } else {
                                """
                                ${getApkVersionText(context)}
                                $curVersionName($curVersionCode) → $versionName($versionCode)
                            """.trimIndent()
                            }
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