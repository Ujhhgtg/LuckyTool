package com.luckyzyx.luckytool.hook.scope.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList

object RemoveMarketDownloadPageAppRecommend : YukiBaseHooker() {
    override fun onHook() {
        //Source DownloadManageFragment
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            dexKitBridge.findMethod {
                searchPackages("com.heytap.cdo.client.ui.downloadmgr")
                matcher {
                    addParamType(ListClass.name)
                    returnType(UnitType)
                    addInvoke {
                        addParamType(ListClass.name)
                        returnType(UnitType)
                        addInvoke {
                            name("notifyDataSetChanged")
                        }
                    }
                }
            }.apply {
                checkDataList("RemoveMarketDownloadPageAppRecommend", false, isDebug = true)
                forEach {
                    it.className.toClass().method { name = it.methodName;param(ListClass) }
                        .hookAll {
                            before {
                                args().first().cast<ArrayList<Any>>()?.clear()
                            }
                        }
                }
            }
        }
    }
}