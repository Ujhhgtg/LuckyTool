package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object ForceDisplayOfRingingStatusToggleTiles : YukiBaseHooker() {
    override fun onHook() {
        //Source QSTileHostHelper
        VariousClass(
            "com.oplusos.systemui.qs.helper.QSTileHostHelper",  //C13
            "com.oplus.systemui.qs.helper.QSTileHostHelper"  //C14
        ).toClass().apply {
            method { name = "getDfltUnsupportedTileString" }.hook {
                after {
                    val list = result<List<String>>() ?: return@after
                    val new = list.toMutableList().apply {
                        remove("ringermode")
                    }
                    result = ArrayList(new)
                }
            }
        }

        //Source RingerModeTile
        VariousClass(
            "com.oplusos.systemui.qs.tiles.FlavorOneRingerModeTile",  //C13
            "com.oplus.systemui.qs.tiles.FlavorOneRingerModeTile",  //C14.0 C14.0.1
            "com.oplus.systemui.qs.tiles.ThreeStageRingerModeTile" //C14.1
        ).toClass().apply {
            //修复实例为Null无法获取spec问题
            constructor().hook {
                after {
                    method { name = "setTileSpec";superClass() }.get(instance).call("ringermode")
                }
            }
            method { name = "isAvailable";superClass(true) }.hook {
                replaceToTrue()
            }
        }

        //Source OplusQSFactoryImpl
        VariousClass(
            "com.oplusos.systemui.qs.qstileimpl.OplusQSFactoryImpl",  //C13
            "com.oplus.systemui.qs.qstileimpl.OplusQSFactoryImpl",  //C14
            "com.oplus.systemui.qs.tileimpl.OplusQSFactoryImpl"  //C15
        ).toClass().apply {
            method { name = "createTile";param(StringClass) }.hook {
                before {
                    val key = args().first().string()
                    if (key == "ringermode") {
                        val provider = field {
//                            name = "mFlavorOneRingerModeTileProvider"
//                            name = "mThreeStageRingerModeTileProvider"
                            name { it.contains("RingerModeTileProvider") }
                        }.get(instance).any() ?: return@before
                        result = provider.current().method {
                            name = "get";returnType = AnyClass
                        }.call()
                    }
                }
            }
        }
    }
}