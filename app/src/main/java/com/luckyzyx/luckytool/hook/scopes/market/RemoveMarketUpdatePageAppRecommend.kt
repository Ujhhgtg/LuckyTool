package com.luckyzyx.luckytool.hook.scopes.market

import android.animation.LayoutTransition
import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.ImageViewClass
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import com.highcapable.yukihookapi.hook.type.android.MotionEventClass
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
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

        //Source APPUpdateItemHolder list_item_product_upgrade -> BaseDownloadItemHolder
        dexKitBridge.findClass {
            searchPackages("com.heytap.cdo.client.ui.upgrademgr")
            matcher {
                fields {
                    addForType(IntType)
                    addForType(BooleanType)
                    addForType(StringClass)
                    addForType(ContextClass)
                    addForType(ViewClass)
                    addForType(ImageViewClass)
                    addForType(TextViewClass)
                    addForType(LayoutInflaterClass)
                    addForType(LayoutTransition::class.java)
                    addForType(View.OnClickListener::class.java)
                }
                methods {
                    add {
                        paramTypes(MapClass)
                        returnType(UnitType)
                    }
                    add {
                        paramTypes(ViewClass, MotionEventClass)
                        returnType(BooleanType)
                        usingNumbers(0, 1)
                    }
                    add {
                        paramTypes(ViewClass)
                        returnType(BooleanType)
                        usingNumbers(0, 1)
                    }
                    add {
                        paramTypes(IntType)
                        returnType(UnitType)
                    }
                    add {
                        paramTypes(BooleanType)
                        returnType(UnitType)
                    }
                }
            }
        }.apply {
            checkDataList("RemoveMarketUpdatePageAppRecommend BaseDownloadItemHolder")
            single().name.toClass().apply {
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