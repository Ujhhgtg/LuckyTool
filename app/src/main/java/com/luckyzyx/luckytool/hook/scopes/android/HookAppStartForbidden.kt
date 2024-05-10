package com.luckyzyx.luckytool.hook.scopes.android

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.AESCrypt.baseDetrypt
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
                            "zh-CN" -> baseDetrypt(
                                "5q2k5bqU55So5a2Y5Zyo6auY5Y2x6aOO6Zmp77yM5bey56aB5q2i5YW26L+Q6KGMI+atpOW6lOeU\n" +
                                        "qOS8muiOt+WPluaJi+acuuacgOmrmOadg+mZkO+8jOWPr+iDveS8muaBtuaEj+aOp+WItuaJi+ac\n" +
                                        "uiPlpb0="
                            )

                            "zh-TW" -> baseDetrypt(
                                "5q2k5oeJ55So5a2Y5Zyo6auY5Y2x6aKo6Zqq77yM5bey56aB5q2i5YW26YGL6KGMI+atpOaHieeU\n" +
                                        "qOacg+eNsuWPluaJi+apn+acgOmrmOasiumZkO+8jOWPr+iDveacg+aDoeaEj+aOp+WItuaJi+ap\n" +
                                        "nyPlpb0="
                            )

                            "en-US" -> baseDetrypt(
                                "VGhpcyBhcHAgaXMgYXQgaGlnaCByaXNrLCBoYXMgYmVlbiBiYW5uZWQgaXRzIG9wZXJhdGlvbiAj\n" +
                                        "IFRoaXMgYXBwIHdpbGwgZ2V0IHRoZSBoaWdoZXN0IGF1dGhvcml0eSBvbiB0aGUgcGhvbmUsIG1h\n" +
                                        "eSBiZSBtYWxpY2lvdXMgY29udHJvbCBwaG9uZSNvaw=="
                            )

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