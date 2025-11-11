package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.core.view.updatePadding
import com.highcapable.hikage.core.base.Hikageable
import com.highcapable.hikage.widget.android.widget.ImageView
import com.highcapable.hikage.widget.android.widget.LinearLayout
import com.highcapable.hikage.widget.android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.safeOf
import com.oplus.util.OplusUnitConversionUtils
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
                    cacheApkInfoMap[instance] = arrayMapOf(
                        "icon" to args(0).int(),
                        "apkPath" to args(1).string(),
                        "label" to args(2).string(),
                        "versionName" to args(3).string(),
                        "versionCode" to args(4).int(),
                        "packageName" to args(5).string(),
                        "size" to args(6).long(),
                    )
                }
            }
        }

        //Source SourceInfo
        "com.android.packageinstaller.oplus.common.SourceInfo".toClass().resolve().apply {
            firstConstructor { parameterCount = 4 }.hook {
                after {
                    cacheSourceInfoMap[instance] = arrayMapOf(
                        "sourcePackage" to args(0).string(),
                        "sourceName" to args(1).string(),
                        "bUnknownSource" to args(2).boolean(),
                        "actionType" to args(3).int(),
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
                    YLog.debug("cacheApkInfo ${cacheApkInfo.toString()}")

                    val cacheSourceInfo = cacheSourceInfoMap[sourceInfo] ?: return@after
                    YLog.debug("cacheSourceInfo ${cacheSourceInfo.toString()}")

                    val actionType = cacheSourceInfo["actionType"] as? Int ?: -1
                    val installSource = cacheSourceInfo["sourceName"] as? String
                        ?: cacheSourceInfo["sourcePackage"] as? String ?: ""

                    val appName = cacheApkInfo["label"] as? String ?: ""
                    val packName = cacheApkInfo["packageName"] as? String ?: ""
                    val versionName = cacheApkInfo["versionName"] as? String ?: ""
                    val versionCode = cacheApkInfo["versionCode"] as? Int ?: -1
                    val apkFilePath = cacheApkInfo["apkPath"] as? String ?: ""
                    val apkSize = cacheApkInfo["size"] as? Long ?: -1

                    val packInfo = PackageUtils(pm).getPackageArchiveInfo(apkFilePath, 1)
                    val newIcon = packInfo?.applicationInfo?.loadIcon(pm)
                    val newMin = packInfo?.applicationInfo?.minSdkVersion
                    val newTarget = packInfo?.applicationInfo?.targetSdkVersion

                    val curPackInfo = PackageUtils(pm).getPackageInfo(packName, 0)
                    val curIcon = curPackInfo?.applicationInfo?.loadIcon(pm)
                    val curVersionName = curPackInfo?.versionName
                    val curVersionCode = curPackInfo?.longVersionCode
                    val curMin = curPackInfo?.applicationInfo?.minSdkVersion
                    val curTarget = curPackInfo?.applicationInfo?.targetSdkVersion

                    val isInstalled = curPackInfo != null
                    val isInstall = actionType == 0
                    val isUninstall = actionType == 1

                    val hikageLayout = Hikageable {
                        LinearLayout(
                            lparams = LayoutParams(widthMatchParent = true),
                            init = {
                                orientation = LinearLayout.VERTICAL
                                gravity = Gravity.CENTER_HORIZONTAL
                                updatePadding(top = 10.dp)
                            }
                        ) {
                            ImageView(
                                id = "app_icon",
                                lparams = LayoutParams(width = 80.dp, height = 80.dp),
                                init = {
                                    setImageDrawable(if (isInstall) newIcon else curIcon)
                                }
                            )
                            TextView(
                                id = "app_name",
                                lparams = LayoutParams(),
                                init = {
                                    text = appName
                                    textSize = 22F
                                    setTextIsSelectable(true)
                                }
                            )
                            TextView(
                                id = "app_packName",
                                lparams = LayoutParams(),
                                init = {
                                    text = packName
                                    setTextIsSelectable(true)
                                }
                            )
                            TextView(
                                id = "app_size",
                                lparams = LayoutParams(),
                                init = {
                                    val format = if (apkSize <= 0) ""
                                    else OplusUnitConversionUtils(context).getUnitValue(apkSize)
                                    text = "${getApkSizeText(context)} $format"
                                    setTextIsSelectable(true)
                                }
                            )
                            TextView(
                                id = "app_version",
                                lparams = LayoutParams(),
                                init = {
                                    text = if (isInstalled) {
                                        if (isUninstall) "$versionName($versionCode)"
                                        else "$curVersionName($curVersionCode) → $versionName($versionCode)"
                                    } else "$versionName($versionCode)"
                                    setTextIsSelectable(true)
                                }
                            )
                            if (isInstall) TextView(
                                id = "app_sdk",
                                lparams = LayoutParams(),
                                init = {
                                    text = if (isInstalled) {
                                        "Min SDK: $curMin → $newMin  |  Target SDK: $curTarget → $newTarget"
                                    } else {
                                        "Min SDK: $newMin  |  Target SDK: $newTarget"
                                    }
                                    setTextIsSelectable(true)
                                }
                            )
                            if (isInstall) TextView(
                                id = "app_from",
                                lparams = LayoutParams(),
                                init = {
                                    text = getInstallSourceText(context, installSource)
                                    setTextIsSelectable(true)
                                }
                            )
                        }
                    }
                    val hikage = hikageLayout.create(context)

                    apkInfoView.removeAllViews()
                    apkInfoView.addView(hikage.root)

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
    private fun getApkSizeText(context: Context): String {
        return safeOf("Size: ") {
            context.resources.getString(
                context.resources.getIdentifier(
                    "app_info_size", "string", context.packageName
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