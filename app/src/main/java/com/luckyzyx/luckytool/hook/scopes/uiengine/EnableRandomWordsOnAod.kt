package com.luckyzyx.luckytool.hook.scopes.uiengine

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.topjohnwu.superuser.ShellUtils

object EnableRandomWordsOnAod : YukiBaseHooker() {
    override fun onHook() {
        val customApi = prefs(ModulePrefs).getString("custom_random_words_api", "")

        //Source AodRootLayout
        "com.oplus.aodimpl.AodRootLayout".toClass().apply {
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
                        val api = customApi.ifBlank { "https://api.7585.net.cn/yan/api.php" }
                        val yiyan = ShellUtils.fastCmd("curl $api")
                        if (yiyan.isNotBlank()) {
                            textMethod.current().method { name = "setXmlValue" }.call(yiyan)
                            textMethod.current().method { name = "setValue" }.call(yiyan)
                        }
                    }
                }
            }
        }
    }
}