package com.luckyzyx.luckytool.hook.scope.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveMarketMinePageAppRecommend(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val viewLayerWrapDto = "com.heytap.cdo.card.domain.dto.ViewLayerWrapDto"
        val mineActionBarView = "com.heytap.market.mine.view.MineActionBarView"

        //Source MineFragment
        dexKitBridge.findClass {
            searchPackages("com.heytap.market.mine")
            matcher {
                fields {
                    addForType(MapClass.name)
                    addForType(StringClass.name)
                    addForType(BooleanType.name)
                    addForType(BundleClass.name)
                    addForType(ContextClass.name)
                    addForType(mineActionBarView)
                }
                methods {
                    add { name("onCreate") }
                    add { name("onCreateView") }
                    add { name("onDestroy") }
                    add { name("onDestroyView") }
                    add { name("onConfigurationChanged") }
//                    add { returnType(mineActionBarView) }
//                    add { returnType(cdoNestedScrollListView) }
                    add {
                        paramTypes(viewLayerWrapDto)
                        returnType(MapClass)
                    }
                    add {
                        paramTypes(viewLayerWrapDto, BooleanType.name)
                        returnType(UnitType)
                    }
                }
                usingStrings("MineFragment")
            }
        }.apply {
            checkDataList("MineFragment")
            single().name.toClass().apply {
                method {
                    param(viewLayerWrapDto, BooleanType)
                    returnType(UnitType)
                }.hook {
                    before {
                        val dto = args().first().any() ?: return@before
                        val cards = dto.current().method { name = "getCards" }
                            .invoke<List<Any>>()?.toMutableList() ?: return@before
                        cards.removeIf { cards.indexOf(it) != 0 }
                        dto.current().method { name = "setCards" }.call(ArrayList(cards))
                    }
                }
            }
        }
    }
}