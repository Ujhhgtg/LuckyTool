package com.luckyzyx.luckytool.hook.scope.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveMarketDownloadPageAppRecommend(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source DownloadManagerAdapter
        dexKitBridge.findMethod {
            searchPackages("com.heytap.cdo.client.ui.downloadmgr")
            matcher {
                addParamType(ListClass.name)
                returnType(UnitType)
                addInvoke {
                    name("notifyDataSetChanged")
                }
                addCall {
                    addParamType(ListClass.name)
                    returnType(UnitType)
                }
            }
        }.apply {
            checkDataList("RemoveMarketDownloadPageAppRecommend", false)
            forEach {
                it.className.toClass().method { name = it.methodName;param(ListClass) }
                    .hook {
                        before {
                            args().first().cast<ArrayList<Any>>()?.clear()
                        }
                    }
            }
        }
    }
}