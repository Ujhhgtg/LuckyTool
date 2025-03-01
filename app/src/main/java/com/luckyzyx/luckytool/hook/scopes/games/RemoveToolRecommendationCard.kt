package com.luckyzyx.luckytool.hook.scopes.games

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveToolRecommendationCard : YukiBaseHooker() {
    override fun onHook() {
        //Source ToolsRecommendCardLayout
        "business.module.toolsrecommend.ToolsRecommendCardLayout".toClassOrNull()?.apply {
            method { param(ListClass);returnType = UnitType }.hook {
                before {
                    args().first().set(ArrayList<Any>())
                }
            }
        }

        //Source GameToolTileAdapter V9.0.0+
//        "business.toolpanel.adapter.GameToolTileAdapter".toClassOrNull()?.apply {
//            method { name = "onCreateViewHolder" }.hook {
//                after {
//                    val parent = args().first().cast<ViewGroup>() ?: return@after
//                    val id = args().last().int()
//                    if (id == 10005) result<ViewHolder>()?.itemView?.isVisible = false
//                }
//            }
//        }
    }
}