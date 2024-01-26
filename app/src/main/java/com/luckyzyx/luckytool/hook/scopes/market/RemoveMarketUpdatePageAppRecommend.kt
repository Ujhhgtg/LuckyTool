package com.luckyzyx.luckytool.hook.scopes.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.android.ViewGroupClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveMarketUpdatePageAppRecommend(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val cardDto = "com.heytap.cdo.card.domain.dto.CardDto"
        val imageLoader = "com.nearme.imageloader.ImageLoader"

        //Source AppUpdateFragment
        dexKitBridge.findMethod {
            searchPackages("com.heytap.cdo.client.ui.upgrademgr")
            matcher {
                addParamType(ListClass)
                returnType(UnitType)
                usingNumbers(114.0F)
                addInvoke {
                    addParamType(ListClass)
                    returnType(UnitType)
                    usingNumbers(0)
                    addInvoke {
                        name("notifyDataSetChanged")
                    }
                }
            }
        }.apply {
            checkDataList("RemoveMarketUpdatePageAppRecommend AppUpdateFragment")
            val member = single()
            member.className.toClass().apply {
                method { name = member.methodName;param(ListClass) }.hook {
                    before {
                        args().first().cast<ArrayList<Any>>()?.clear()
                    }
                }
            }
        }

        //Source APPUpdateItemHolder list_item_product_upgrade
        dexKitBridge.findClass {
            searchPackages("com.heytap.cdo.client.ui.upgrademgr")
            matcher {
                fields {
                    addForType(ViewGroupClass)
                    addForType(TextViewClass)
                    addForType(imageLoader)
                }
                methods {
                    add {
                        paramTypes(ContextClass, IntType)
                        returnType(UnitType)
                    }
                    add {
                        paramTypes(
                            ContextClass, StringClass, IntType, IntType
                        )
                        returnType(UnitType)
                    }
                    add {
                        paramTypes(ViewClass, BooleanType)
                        returnType(UnitType)
                    }
                    add {
                        paramCount(0)
                        returnType(ViewClass)
                    }
                    add {
                        paramTypes(LayoutInflaterClass);returnType(ViewClass)
                    }
                }
            }
        }.apply {
            checkDataList("RemoveMarketUpdatePageAppRecommend APPUpdateItemHolder")
            single().name.toClass().apply {
                if (hasMethod {
                        param(cardDto, StringClass, VagueType, MapClass, BooleanType, LongType)
                        returnType(UnitType)
                    }) {
                    method {
                        param(cardDto, StringClass, VagueType, MapClass, BooleanType, LongType)
                        returnType(UnitType)
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}