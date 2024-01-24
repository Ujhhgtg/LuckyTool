package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method

object ForceDisplayOfRingingStatusToggleTiles : YukiBaseHooker() {
    override fun onHook() {
        //Source QSTileHostHelper
        "com.oplus.systemui.qs.helper.QSTileHostHelper".toClass().apply {
            method { name = "getDfltUnsupportedTileString" }.hook {
                after {
                    val list = result<List<String>>()?.toMutableList()?.apply {
                        remove("ringermode")
                    } ?: return@after
                    result = ArrayList<String>(list)
                }
            }
        }

        //Source OplusQSFactoryImpl
        "com.oplus.systemui.qs.qstileimpl.OplusQSFactoryImpl".toClass().apply {
            method { name = "createTileInternal" }.hook {
                before {
                    val key = args().first().string()
                    if (key == "ringermode") {
                        val provider = field {
                            name = "mFlavorOneRingerModeTileProvider"
                        }.get(instance).any() ?: return@before
                        result = provider.current().method { name = "get" }.call()
                    }
                }
            }
        }

        //Source RingerModeTile
        "com.oplus.systemui.qs.tiles.FlavorOneRingerModeTile".toClass().apply {
            method { name = "isAvailable";superClass(true) }.hook {
                replaceToTrue()
            }
        }
    }
}