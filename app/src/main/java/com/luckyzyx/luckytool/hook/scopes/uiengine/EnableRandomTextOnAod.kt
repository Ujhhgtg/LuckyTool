package com.luckyzyx.luckytool.hook.scopes.uiengine

import com.drake.net.Get
import com.drake.net.okhttp.trustSSLCertificate
import com.drake.net.utils.scopeNet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.ModulePrefs
import java.io.File

@Obfuscate
object EnableRandomTextOnAod : YukiBaseHooker() {
    override fun onHook() {
        val mode = prefs(ModulePrefs).getString("set_random_text_display_mode", "0")

        val customFile = prefs(ModulePrefs).getString("custom_random_text_file", "")
        val customApi = prefs(ModulePrefs).getString("custom_random_text_api", "")

        val yiyanTextArrayCache = ArrayList<String>()
        var yiyanTextCache = ""

        //Source AodRootLayout
        "com.oplus.aodimpl.AodRootLayout".toClass().apply {
            constructor { paramCount = 2 }.hook {
                before {
                    when (mode) {
                        "1" -> {
                            if (yiyanTextArrayCache.isEmpty()) {
                                if (customFile.isBlank()) return@before
                                val file = File(customFile)
                                if (file.exists().not()) return@before
                                yiyanTextArrayCache.addAll(file.readLines())
                            }
                        }

                        "2" -> {
                            if (customApi.isBlank()) return@before
                            if ((customApi.startsWith("http://") || customApi.startsWith("https://")).not()) return@before
                            scopeNet {
                                val text = Get<String>(customApi) {
                                    setClient {
                                        trustSSLCertificate()
                                    }
                                }.await()
                                if (text.isNotBlank()) yiyanTextCache = text
                            }.catch {
                                return@catch
                            }
                        }
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

                        when (mode) {
                            "1" -> {
                                if (yiyanTextArrayCache.isNotEmpty()) {
                                    textMethod.current().method { name = "setXmlValue" }
                                        .call(yiyanTextArrayCache.shuffled().first())
                                    textMethod.current().method { name = "setValue" }
                                        .call(yiyanTextArrayCache.shuffled().first())
                                }
                            }

                            "2" -> if (yiyanTextCache.isNotBlank()) {
                                textMethod.current().method { name = "setXmlValue" }
                                    .call(yiyanTextCache)
                                textMethod.current().method { name = "setValue" }
                                    .call(yiyanTextCache)
                            }
                        }
                    }
                }
            }
        }
    }
}