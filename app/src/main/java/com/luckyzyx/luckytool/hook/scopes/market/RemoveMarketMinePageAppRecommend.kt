package com.luckyzyx.luckytool.hook.scopes.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class RemoveMarketMinePageAppRecommend(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val viewLayerWrapDto = "com.heytap.cdo.card.domain.dto.ViewLayerWrapDto"
        val mineActionBarView = "com.heytap.market.mine.view.MineActionBarView"

        //Source MineFragment
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(MapClass)
                    addForType(StringClass)
                    addForType(BooleanType)
                    addForType(BundleClass)
                    addForType(ContextClass)
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
                        val cards = dto.current().method {
                            name = "getCards"
                        }.invoke<List<Any>>()?.toMutableList()?.apply {
                            removeIf { indexOf(it) != 0 }
                        } ?: return@before
                        dto.current().method { name = "setCards" }.call(ArrayList(cards))
                    }
                }
            }
        }
    }
}