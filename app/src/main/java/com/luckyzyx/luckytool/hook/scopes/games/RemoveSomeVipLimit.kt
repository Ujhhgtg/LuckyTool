package com.luckyzyx.luckytool.hook.scopes.games

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object RemoveSomeVipLimit : YukiBaseHooker() {
    override fun onHook() {
        //network_speed_vip -> oppo_acc

        //Source VipInfoBean -> VipInfosDTO
        //<string name="magic_voice_buy_vip_tip">开启游戏变声，尽享全部变声效果</string>
        "com.oplus.games.account.bean.VipInfoBean\$VipInfosDTO".toClass().apply {
            method { name = "getVip" }.hook {
                replaceToTrue()

            }
            method { name = "getExpiredVip" }.hook {
                replaceToFalse()
            }
            method { name = "getExpireTime" }.hook {
                replaceTo("2999-12-31")
            }
            method { name = "getSign" }.hook {
                replaceToTrue()
            }
        }
        //Source VipAccelearateResponse
        "com.oplus.games.account.bean.VipAccelearateResponse".toClass().apply {
            method { name = "getSuperBooster" }.hook {
                replaceToTrue()
            }
            method { name = "isSuperBooster" }.hook {
                replaceToTrue()
            }
        }
        //Source VIPStateBean
        "com.oplus.games.account.bean.VIPStateBean".toClass().apply {
            method { name = "getVipState" }.hook {
                replaceTo(5)
            }
            method { name = "getExpireTime" }.hook {
                replaceTo("2999-12-31")
            }
        }
        //Source UserInfo
        "com.coloros.gamespaceui.module.magicvoice.oplus.data.UserInfo".toClass().apply {
            method { name = "getExpireTime" }.hook {
                replaceTo("2999-12-31")
            }
            method { name = "getHasTrialQualifications" }.hook {
                replaceToTrue()
            }
            method { name = "getUserIdentity" }.hook {
                replaceTo(3)
            }
        }
    }
}