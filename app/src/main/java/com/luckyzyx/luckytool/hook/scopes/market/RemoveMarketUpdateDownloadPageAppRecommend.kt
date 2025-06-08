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
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveMarketUpdateDownloadPageAppRecommend(val dexKitBridge: DexKitBridge) :
    YukiBaseHooker() {
    override fun onHook() {
        val cardDto = "com.heytap.cdo.card.domain.dto.CardDto"
        val imageLoader = "com.nearme.imageloader.ImageLoader"

        //Source CardDataProcessor
        dexKitBridge.findClass {
            matcher {
                addFieldForName("mDataUtil")
                addMethod {
                    name("processData")
//                    paramTypes(ListClass, IntType, null)
                    returnType(ListClass)
                }
            }
        }.apply {
            checkDataList("RemoveMarketUpdateDownloadPageAppRecommend")
            single().name.toClass().apply {
                method { name = "processData" }.hook {
                    after {
                        result<ArrayList<Any>>()?.clear()
                    }
                }
            }
        }

        //Source APPUpdateItemHolder list_item_product_upgrade
        dexKitBridge.findClass {
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
                val hasMethod = hasMethod {
                    param(cardDto, StringClass, VagueType, MapClass, BooleanType, LongType)
                    returnType(UnitType)
                }
                if (hasMethod) {
                    method {
                        param(cardDto, StringClass, VagueType, MapClass, BooleanType, LongType)
                        returnType(UnitType)
                    }.hook {
                        intercept()
                    }
                }
            }
        }

        //Source AppUpdateFragmentV2
        "com.heytap.cdo.client.ui.upgrademgrv2.AppUpdateFragmentV2".toClassOrNull()?.apply {
            dexKitBridge.findClass {
                matcher {
                    className(name)
                }
            }.apply {
                checkDataList("RemoveMarketUpdatePageAppRecommend AppUpdateFragmentV2")
                findMethod {
                    matcher {
                        paramTypes(ListClass)
                        addInvoke {
                            paramTypes(ContextClass, FloatType)
                            returnType(IntType)
                        }
                        usingNumbers(114.0F)
                    }
                }.apply {
                    checkDataList("RemoveMarketUpdatePageAppRecommend addDataAndNotifyChanged")
                    method {
                        name = single().name
                        param(ListClass)
                    }.hook {
                        before {
                            args().first().cast<java.util.ArrayList<Any>>()?.clear()
                        }
                    }
                }

                findMethod {
                    matcher {
                        paramTypes(BooleanType)
                        addCaller {
                            paramTypes(IntType)
                            usingNumbers(1002, 1003)
                        }
                        usingNumbers(0, 300L)
                        usingStrings("mRecommendUpdateContainer", "mNormalUpdateContainer")
                    }
                }.apply {
                    checkDataList("RemoveMarketUpdatePageAppRecommend AutoScrollWhenUpdateAll")
                    method {
                        name = single().name
                        param(BooleanType)
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}