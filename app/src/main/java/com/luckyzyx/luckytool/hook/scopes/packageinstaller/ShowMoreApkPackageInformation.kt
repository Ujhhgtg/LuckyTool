package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.safeOf
import com.luckyzyx.luckytool.utils.safeOfNull
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.result.ClassData
import org.luckypray.dexkit.result.MethodData

@Obfuscate
class ShowMoreApkPackageInformation(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    lateinit var apkInfoViewCls: ClassData
    lateinit var loadApkInfo: MethodData

    var cacheApkInfoMap = ArrayMap<Any, ArrayMap<String, Any>>()
    var cacheSourceInfoMap = ArrayMap<Any, ArrayMap<String, Any>>()

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        //Source ApkInfoView
        apkInfoViewCls = dexKitBridge.findClass {
            matcher {
                className("com.android.packageinstaller.oplus.view.ApkInfoView")
            }
        }.apply {
            checkDataList("ApkInfoView")
            loadApkInfo = findMethod {
                matcher {
                    paramCount(2)
                    returnType(Void.TYPE)
                    usingNumbers(0, 1, 8)
                }
            }.apply {
                checkDataList("loadApkInfo")
            }.singleOrNull() ?: return
        }.singleOrNull() ?: return

        //Source ApkInfo
        "com.android.packageinstaller.oplus.common.ApkInfo".toClass().resolve().apply {
            firstConstructor { parameterCount = 7 }.hook {
                after {
                    cacheApkInfoMap.put(
                        instance, arrayMapOf(
                            "icon" to args(0).int(),
                            "apkPath" to args(1).string(),
                            "label" to args(2).string(),
                            "versionName" to args(3).string(),
                            "versionCode" to args(4).int(),
                            "packageName" to args(5).string(),
                            "size" to args(6).long(),
                        )
                    )
                }
            }
        }

        //Source SourceInfo
        "com.android.packageinstaller.oplus.common.SourceInfo".toClass().resolve().apply {
            firstConstructor { parameterCount = 4 }.hook {
                after {
                    cacheSourceInfoMap.put(
                        instance, arrayMapOf(
                            "sourcePackage" to args(0).string(),
                            "sourceName" to args(1).string(),
                            "bUnknownSource" to args(2).boolean(),
                            "actionType" to args(3).int(),
                        )
                    )
                }
            }
        }

        //Source ApkInfoView
        apkInfoViewCls.name.toClass().resolve().apply {
            firstMethod {
                name = loadApkInfo.name
                parameterCount = 2
            }.hook {
                after {
                    val apkInfoView = instance<LinearLayout>()
                    val context = apkInfoView.context
                    val pm = context.packageManager

                    val apkInfo = args().first().any() ?: return@after
                    val sourceInfo = args().last().any() ?: return@after

                    val cacheApkInfo = cacheApkInfoMap[apkInfo] ?: return@after
                    val cacheSourceInfo = cacheSourceInfoMap[sourceInfo] ?: return@after

                    val actionType = cacheSourceInfo["actionType"] as? Int ?: -1
                    val installSource = cacheSourceInfo["sourceName"] as? String
                        ?: cacheSourceInfo["sourcePackage"] as? String ?: ""

                    val packName = cacheApkInfo["packageName"] as? String ?: ""
                    val versionName = cacheApkInfo["versionName"] as? String ?: ""
                    val versionCode = cacheApkInfo["versionCode"] as? Int ?: -1
                    val apkFilePath = cacheApkInfo["apkPath"] as? String ?: ""

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

                    val mApkIcon = apkInfoView.findViewById<ImageView>(
                        apkInfoView.resources.getIdentifier(
                            "app_icon", "id",
                            this@ShowMoreApkPackageInformation.packageName
                        )
                    )?.apply {
                        safeOfNull { parent as ViewGroup }?.removeView(this)
                    } ?: return@after
                    newApkHeaderView.addView(mApkIcon)

                    val newApkNameView = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                    }
                    val mApkName = apkInfoView.findViewById<TextView>(
                        apkInfoView.resources.getIdentifier(
                            "app_name", "id",
                            this@ShowMoreApkPackageInformation.packageName
                        )
                    )?.apply {
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

                    val mAppSize = apkInfoView.findViewById<TextView>(
                        apkInfoView.resources.getIdentifier(
                            "app_size", "id",
                            this@ShowMoreApkPackageInformation.packageName
                        )
                    )?.apply {
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

                    cacheApkInfoMap.clear()
                    cacheSourceInfoMap.clear()
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