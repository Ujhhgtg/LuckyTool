package com.luckyzyx.luckytool.hook.scopes.packageinstaller

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.core.view.isVisible
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType

@Obfuscate
class RemoveInstallAds(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    var activity: Activity? = null

    override fun onHook() {
        //Source InstallAppProgress
        dexKitBridge.findClass {
            matcher {
                className(
                    "com.android.packageinstaller.oplus.InstallAppProgress",
                    StringMatchType.StartsWith
                )
            }
        }.apply {
            checkDataList("InstallAppProgress", onlyOne = false)
            findMethod {
                matcher {
                    paramCount(0)
                    returnType(Void.TYPE)
                    addCaller { name("onCreate") }
                    usingStrings("source_info", "type_channel_title", "type_channel_tips")
                }
            }.apply {
                checkDataList("initView")
                single().className.toClass().resolve().apply {
                    firstMethod { name = single().methodName }.hook {
                        after {
                            activity = instance<Activity>()
                            activity?.removeViews()
                        }
                    }
                }
            }

            findMethod {
                matcher {
                    name("handleMessage")
                    usingStrings(
                        "oplus.intent.action.VIRUS_APK_INSTALLED",
                        "oplus.permission.OPLUS_COMPONENT_SAFE"
                    )
                }
            }.apply {
                checkDataList("handleMessage")
                single().className.toClass().resolve().apply {
                    firstMethod { name = single().methodName }.hook {
                        after {
                            activity?.removeViews()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun Activity.removeViews() {
        findViewById<View>(
            resources.getIdentifier(
                "suggest_A_scroll_layout", "id",
                this@RemoveInstallAds.packageName
            )
        )?.isVisible = false
        findViewById<View>(
            resources.getIdentifier(
                "install_done_suggest_B", "id",
                this@RemoveInstallAds.packageName
            )
        )?.isVisible = false
    }
}