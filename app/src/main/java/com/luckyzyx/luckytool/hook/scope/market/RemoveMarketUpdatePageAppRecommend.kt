package com.luckyzyx.luckytool.hook.scope.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.highcapable.yukihookapi.hook.type.android.ValueAnimatorClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.android.ViewGroupClass
import com.highcapable.yukihookapi.hook.type.android.ViewGroup_LayoutParamsClass
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.query.matchers.MethodMatcher

object RemoveMarketUpdatePageAppRecommend : YukiBaseHooker() {
    override fun onHook() {
        //Source AppUpdateFragment
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            dexKitBridge.findMethod {
                searchPackages("com.heytap.cdo.client.ui.upgrademgr")
                matcher {
                    addParamType(ListClass.name)
                    returnType(UnitType.name)
                    addInvoke(MethodMatcher().name("notifyDataSetChanged"))
                    addCall {
                        addParamType(ListClass.name)
                        returnType(UnitType.name)
                        usingNumbers(114.0F)
                    }
                }
            }.apply {
                checkDataList("RemoveMarketUpdatePageAppRecommend AppUpdateFragment")
                val member = first()
                member.className.toClass().apply {
                    method { name = member.methodName;param(ListClass) }.hook {
                        before {
                            val list = args().first().list<Any>().toMutableList()
                            list.clear()
                            args().first().set(ArrayList(list))
                        }
                    }
                }
            }
        }
        val cardDto = "com.heytap.cdo.card.domain.dto.CardDto"
        val imageLoader = "com.nearme.imageloader.ImageLoader"
        //Source APPUpdateItemHolder list_item_product_upgrade
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            dexKitBridge.findClass {
                searchPackages("com.heytap.cdo.client.ui.upgrademgr")
                matcher {
                    fields {
                        addForType(BooleanType.name)
                        addForType(ViewGroupClass.name)
                        addForType(TextViewClass.name)
                        addForType(imageLoader)
                    }
                    methods {
                        add {
                            paramTypes(ContextClass.name, IntType.name)
                            returnType(UnitType.name)
                        }
                        add {
                            paramTypes(
                                ContextClass.name, StringClass.name, IntType.name, IntType.name
                            )
                            returnType(UnitType.name)
                        }
                        add {
                            paramTypes(ViewClass.name, BooleanType.name, BooleanType.name)
                            returnType(UnitType.name)
                        }
                        add {
                            paramTypes(LayoutInflaterClass.name);returnType(ViewClass.name)
                        }
                        add {
                            paramTypes(ViewGroup_LayoutParamsClass.name, ValueAnimatorClass.name)
                            returnType(UnitType.name)
                        }

                    }
                }
            }.apply {
                checkDataList("RemoveMarketUpdatePageAppRecommend APPUpdateItemHolder")
                first().name.toClass().apply {
                    method {
                        param(cardDto, StringClass, VagueType, MapClass, BooleanType, LongType)
                        returnType(UnitType)
                    }.hook { intercept() }
                }
            }
        }
    }
}