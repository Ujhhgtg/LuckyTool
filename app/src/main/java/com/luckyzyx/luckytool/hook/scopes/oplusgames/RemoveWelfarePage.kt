package com.luckyzyx.luckytool.hook.scopes.oplusgames

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveWelfarePage(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val mainPanelView = "business.mainpanel.MainPanelView".toClassOrNull()
        if (mainPanelView == null) {
            "business.mainpanel.main.MainPanelFragment".toClass().apply {
                method { name = "addRadioButton" }.hook {
                    before {
                        if (args().first().string() == "welfare") resultNull()
                    }
                }
                method { name = "initView" }.hook {
                    after {
                        field { name = "navButtonMap" }.get(instance).cast<HashMap<String, Any>>()
                            ?.remove("welfare")
                    }
                }
            }
            dexKitBridge.findField {
                searchPackages("business.mainpanel.view.NavigationRadioButton")
                matcher {
                    type(StringClass)
                    addReadMethod {
                        paramTypes(null, BooleanType, BooleanType)
                        returnType(UnitType)
                        usingStrings("perf", "tool")
                    }
                    declaredClass {
                        usingStrings("NavigationRadioButton")
                    }
                }
            }.apply {
                checkDataList("RemoveWelfarePage NavigationRadioButton")
                single().className.toClass().apply {
                    method {
                        param(VagueType, BooleanType, BooleanType)
                        returnType = UnitType
                    }.hook {
                        before {
                            val type = field { name = single().fieldName }.get(instance).string()
                            if (type == "welfare") instance<View>().isVisible = false
                        }
                    }
                }
            }
            return
        }

        //Source MainPanelView
        mainPanelView.apply {
            method {
                param { it[0] == ListClass && it[1] == BooleanType }
                paramCount(2..3)
                returnType = UnitType
            }.hook {
                before {
                    val list = args().first().list<Any>()
                    val first = list.getOrNull(0) ?: return@before
                    args().first().set(ArrayList(arrayListOf(first)))
                }
            }
        }
    }
}