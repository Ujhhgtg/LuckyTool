package com.luckyzyx.luckytool.hook.scopes.uiengine

import com.drake.net.Get
import com.drake.net.utils.scopeNet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs

object EnableRandomWordsOnAod : YukiBaseHooker() {
    override fun onHook() {
        val customApi = prefs(ModulePrefs).getString("custom_random_words_api", "")

        var yiyanText = ""

        //Source AodRootLayout
        "com.oplus.aodimpl.AodRootLayout".toClass().apply {
            constructor { paramCount = 2 }.hook {
                before {
                    if (customApi.isBlank()) return@before
                    if ((customApi.startsWith("http://") || customApi.startsWith("https://")).not()) return@before
                    scopeNet {
                        val text = Get<String>(customApi).await()
                        if (text.isNotBlank()) yiyanText = text
                    }.catch {
                        return@catch
                    }
                }
            }
            method { name = "getCustomView" }.hook {
                before {
                    val viewBean = args().first().any() ?: return@before
                    val mViewType = viewBean.current().method { name = "getViewType" }.string()
                    if (mViewType != "AodTextView") return@before

                    val mMethodBeanList = viewBean.current().field { name = "mMethodBeanList" }
                        .list<Any>()
                    val textMethod = mMethodBeanList.find {
                        it.current().method { name = "getXmlAttribute" }.string() == "text" &&
                                it.current().method { name = "getMethodName" }.string() == "setText"
                    }
                    if (textMethod != null) {
                        if (yiyanText.isNotBlank()) {
                            textMethod.current().method { name = "setXmlValue" }.call(yiyanText)
                            textMethod.current().method { name = "setValue" }.call(yiyanText)
                        }
                    }
                }
            }
        }
    }
}