package com.luckyzyx.luckytool.hook.scope.browser

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LinearLayoutClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveAdsAtDownloadPageBottom(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val recommendConfig = "com.heytap.browser.downloads.entity.RecommendConfig"
        val cOUITabLayout = "com.coui.appcompat.tablayout.COUITabLayout"

        //Source AppRecommendManager -> LinearLayout setVisibility 0/8 500L
        dexKitBridge.findMethod {
            matcher {
                paramCount(0)
                returnType(UnitType)
                usingNumbers(0, 8, 500L)
                addUsingField {
                    field {
                        addPutMethod {
                            paramTypes(recommendConfig)
                            returnType(UnitType)
                        }
                        type(recommendConfig)
                    }
                }
                addUsingField {
                    field {
                        addPutMethod {
                            paramCount(0)
                            returnType(UnitType)
                        }
                        type(LinearLayoutClass)
                    }
                }
                addUsingField {
                    field {
                        addPutMethod {
                            paramCount(0)
                            returnType(UnitType)
                        }
                        type(cOUITabLayout)
                    }
                }
            }
        }.apply {
            checkDataList("RemoveAdsAtDownloadPageBottom")
            val member = first()
            member.className.toClass().apply {
                method {
                    name = member.methodName
                    emptyParam()
                    returnType(UnitType)
                }.hook {
                    replaceUnit {
                        field { type(LinearLayoutClass) }.get(instance).cast<View>()
                            ?.isVisible = false
                    }
                }
            }
        }
    }
}