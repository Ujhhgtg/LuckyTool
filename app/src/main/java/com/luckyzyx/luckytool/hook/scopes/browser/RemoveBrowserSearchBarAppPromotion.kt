package com.luckyzyx.luckytool.hook.scopes.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ArrayListClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveBrowserSearchBarAppPromotion(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val appHostCls = "com.heytap.browser.platform.app.AppHost"
        if (appHostCls.toClassOrNull() == null) return

        //Source MultiSugItemData
        val app = dexKitBridge.findClass {
            matcher {
                addFieldForType(StringClass)
                addMethod { paramCount(0);returnType(StringClass) }
                usingStrings("res", "initialState", "sugNaturalApp")
            }
        }.checkDataList("RemoveBrowserSearchBarAppPromotion App")

        //Source MultiSugItemData
        val ads = dexKitBridge.findClass {
            matcher {
                addFieldForType(IntType)
                addFieldForType(StringClass)
                addMethod { name("getTitle") }
                addMethod { name("getCategoryType") }
                addMethod { paramCount(0);returnType(IntType) }
                addMethod { paramCount(0);returnType(StringClass) }
                usingStrings("res", "ad", "sugAd")
            }
        }.checkDataList("RemoveBrowserSearchBarAppPromotion Ads")

        //Source SugMultiAdapter
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ListClass)
                    addForType(ArrayListClass)
                    addForType(MapClass)
                    addForType(IntType)
                    addForType(appHostCls)
                }
                methods {
                    add { paramCount(0);returnType(ListClass) }
                    add { paramTypes(IntType, IntType, IntType, IntType) }
                    add { paramTypes(ListClass);returnType(UnitType) }
                    add { paramTypes(appHostCls);returnType(UnitType) }
                    add { name("getItemCount") }
                    add { name("getItemViewType") }
                    add { name("onAttachedToRecyclerView") }
                    add { name("onBindViewHolder") }
                    add { name("onCreateViewHolder") }
                    add { name("onViewAttachedToWindow") }
                }
            }
        }.apply {
            checkDataList("RemoveBrowserSearchBarAppPromotion Adapter")
            findMethod {
                matcher {
                    paramTypes(ListClass)
                    returnType(UnitType)
                    usingStrings("linkEdit")
                    addCaller {
                        paramTypes(ListClass)
                        returnType(UnitType)
                        usingStrings("headerData", "linkEdit")
                    }
                }
            }.apply {
                checkDataList("RemoveBrowserSearchBarAppPromotion Method")
                single().className.toClass().apply {
                    method {
                        name = single().methodName
                        param(ListClass)
                    }.hook {
                        before {
                            val list = args().first().cast<ArrayList<Any>>() ?: return@before
                            list.removeIf {
//                                YLog.debug("${list.indexOf(it)} -> $it")
                                it.javaClass.name == app.singleOrNull()?.name || it.javaClass.name == ads.singleOrNull()?.name
                            }
                        }
                    }
                }
            }
        }
    }
}