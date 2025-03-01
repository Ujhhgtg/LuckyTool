package com.luckyzyx.luckytool.hook.scopes.browser

import android.view.View
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LinearLayoutClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveAdsAtDownloadPageBottom(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val recommendConfig = VariousClass(
            "com.heytap.browser.downloads.entity.RecommendConfig",  //v40.8.24.1
            "com.heytap.browser.download.ui.downloadlist.model.RecommendConfig"  //v40.8.25.1
        ).toClass()

        //Source AppRecommendManager -> LinearLayout setVisibility 0/8 500L
        dexKitBridge.findMethod {
            matcher {
                paramCount(0)
                returnType(UnitType)
                usingNumbers(0, 8, 500L)
                usingFields {
                    add {
                        type(recommendConfig)
                        addWriteMethod {
                            paramTypes(recommendConfig)
                            returnType(UnitType)
                        }
                    }
                    add {
                        type(LinearLayoutClass)
                        addWriteMethod {
                            paramCount(0)
                            returnType(UnitType)
                        }
                    }
                }
            }
        }.apply {
            checkDataList("RemoveAdsAtDownloadPageBottom")
            single().className.toClass().apply {
                method {
                    name = single().methodName
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