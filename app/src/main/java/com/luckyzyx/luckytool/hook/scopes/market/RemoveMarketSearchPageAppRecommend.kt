package com.luckyzyx.luckytool.hook.scopes.market

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.extends
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class RemoveMarketSearchPageAppRecommend(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val cardDto = "com.heytap.cdo.card.domain.dto.CardDto"
        val viewLayerWrapDto = "com.heytap.cdo.card.domain.dto.ViewLayerWrapDto"
        val resourceDto = "com.heytap.cdo.common.domain.dto.ResourceDto"
        val appListCardDto = "com.heytap.cdo.card.domain.dto.AppListCardDto"

        val horizontalAppItemView = "com.nearme.cards.widget.view.HorizontalAppItemView"

        //Source IAppCard
        val cardMethod = dexKitBridge.findClass {
            matcher {
                className("com.nearme.cards.widget.card.Card")
            }
        }.let {
            it.checkDataList("RemoveMarketSearchPageAppRecommend CardCls")
            it.findMethod {
                matcher {
                    paramTypes(cardDto)
                    returnType(UnitType)
                }
            }.checkDataList("RemoveMarketSearchPageAppRecommend CardMethod").single()
        }

        //Source HorizontalAppCard
        "com.nearme.cards.widget.card.impl.horizontalapp.HorizontalAppCard".toClass().apply {
            method {
                name { it != cardMethod.methodName }; param(cardDto);returnType = UnitType
            }.hook {
                before {
                    args().first().setNull()
                }
            }
            method {
                name = cardMethod.methodName; param(cardDto);returnType = UnitType
            }.hook {
                after {
                    val dto = args().first().any() ?: return@after
                    val viewGroup = field { type = horizontalAppItemView }.get(instance)
                        .cast<ViewGroup>()

                    YLog.debug("${dto.toString()}")

                    val code = dto.current().method { name = "getCode";superClass() }.int()
                    val key = dto.current().method { name = "getKey";superClass() }.int()
                    YLog.debug("code: $code | key: $key")

                    val parent = viewGroup?.parent
                    when (key) {
                        //搜索页
                        56432, 56433 -> {
                            if (parent is ViewGroup) {
                                parent.isVisible = false
                            }
                        }

                        //详情页
                        57963, 54067 -> {
                            if (parent is ViewGroup) {
                                parent.isVisible = false
                            }
                        }

                    }
                }
            }
        }

        //热门App合集
        //Source SearchHotInstallRecycleCard
        "com.nearme.cards.widget.card.impl.search.SearchHotInstallRecycleCard".toClass().apply {
            method { param(cardDto);returnType = UnitType }.hook {
                before {
                    val dto = args().first().any() ?: return@before
                    if (dto.javaClass extends appListCardDto.toClass()) {
                        args().first().setNull()
                    }
                }
            }
        }
    }
}