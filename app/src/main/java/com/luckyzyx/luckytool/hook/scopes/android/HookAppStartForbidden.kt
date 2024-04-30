package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.AppAnalyticsUtils

object HookAppStartForbidden : YukiBaseHooker() {
    override fun onHook() {

        //Source OplusAppStartupConfig
        "com.android.server.am.OplusAppStartupConfig".toClassOrNull()?.apply {
            method { name = "isAppStartForbidden" }.hook {
                after {
                    val packName = args().first().string()
                    if (AppAnalyticsUtils.isAppForbidden(packName)) resultTrue()
                }
            }
            method { name = "handleAppStartForbidden" }.hook {
                after {
                    val packName = args().first().string()
                    if (AppAnalyticsUtils.isAppForbidden(packName)) {
                        val curLanguage = method { name = "getCurrentLanguage" }.get(instance)
                            .invoke<String>() ?: ""
                        val dialogText = when (curLanguage) {
                            "zh-CN" -> "此应用存在高危风险，已禁止其运行#此应用会获取手机最高权限，可能会恶意控制手机#好"
                            "zh-TW" -> "此應用存在高危風險，已禁止其運行#此應用會獲取手機最高權限，可能會惡意控制手機#好"
                            "en-US" -> "This app is at high risk, has been banned its operation # This app will get the highest authority on the phone, may be malicious control phone#ok"
                            else -> return@after
                        }
                        method { name = "parseForbidText" }.get(instance).call(dialogText)
                    }
                }
            }
        }

        //Source OplusListManagerImpl
        "com.android.server.OplusListManagerImpl".toClassOrNull()?.apply {
            method { name = "isAppStartForbidden" }.hook {
                after {
                    val packName = args().first().string()
                    if (AppAnalyticsUtils.isAppForbidden(packName)) resultTrue()
                }
            }
        }
    }
}