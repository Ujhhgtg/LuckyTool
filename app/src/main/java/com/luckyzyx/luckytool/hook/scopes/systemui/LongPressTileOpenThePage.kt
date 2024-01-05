package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Intent
import android.os.Handler
import android.telephony.SubscriptionManager
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method

object LongPressTileOpenThePage : YukiBaseHooker() {
    override fun onHook() {
        //QSTileImpl
        "com.android.systemui.qs.tileimpl.QSTileImpl".toClass().apply {
            method { name = "longClick";paramCount = 1 }.hook {
                before {
                    field { name = "mClickHandler" }.get(instance).cast<Handler>()
                        ?.sendEmptyMessage(4)
                    resultNull()
                }
            }
        }
        //Source OplusCellularTile
        VariousClass(
            "com.oplusos.systemui.qs.tiles.OplusCellularTile", //C13
            "com.oplus.systemui.qs.tiles.OplusCellularTile" //C14
        ).toClass().apply {
            method { name = "getLongClickIntent" }.hook {
                before {
                    val mLockSimState = field { name = "mLockSimState" }.get(instance).boolean()
                    if (mLockSimState) return@before
                    val intent = Intent("android.settings.NETWORK_OPERATOR_SETTINGS")
                    val subId = SubscriptionManager.getDefaultDataSubscriptionId()
                    if (subId != -1) intent.putExtra("android.provider.extra.SUB_ID", subId)
                    result = intent
                }
            }
        }
    }
}