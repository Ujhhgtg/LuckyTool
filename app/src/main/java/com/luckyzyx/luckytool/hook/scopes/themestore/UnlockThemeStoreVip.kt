package com.luckyzyx.luckytool.hook.scopes.themestore

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object UnlockThemeStoreVip : YukiBaseHooker() {
    override fun onHook() {
        //Source VipUserDto
        "com.oppo.cdo.card.theme.dto.vip.VipUserDto".toClassOrNull()?.apply {
            method { name = "getVipStatus" }.hook {
                replaceTo(1)
            }
            method { name = "getVipDays" }.hook {
                replaceTo(999)
            }
        }

        //Source WeatherPageResponseDto
        "com.oppo.cdo.card.theme.dto.page.WeatherPageResponseDto".toClassOrNull()?.apply {
            method { name = "getVipStatus" }.hook {
                replaceTo(1)
            }
        }

        //Source ResourceItemDto
        "com.oppo.cdo.theme.domain.dto.response.ResourceItemDto".toClassOrNull()?.apply {
            method { name = "getIsVip" }.hook {
                replaceTo(1)
            }
            method { name = "getIsVipAvailable" }.hook {
                replaceTo(1)
            }
        }

        //Source PublishProductItemDto
        "com.oppo.cdo.theme.domain.dto.response.PublishProductItemDto".toClassOrNull()?.apply {
            method { name = "getPrice" }.hook {
                replaceTo(0.0)
            }
            method { name = "getIsVipAvailable" }.hook {
                replaceTo(1)
            }
        }

        //Source SplashDto
        "com.oppo.cdo.card.theme.dto.SplashDto".toClassOrNull()?.apply {
            method { name = "getAdData" }.hook {
                replaceTo(null)
            }
            method { name = "getShowTime" }.hook {
                replaceTo(1)
            }
            method { name = "getIsSkip" }.hook {
                replaceToTrue()
            }
        }

        //Source ThemeTrialExpireReceiver
        "com.nearme.themespace.trial.ThemeTrialExpireReceiver".toClassOrNull()?.apply {
            method { name = "onReceive" }.hook {
                intercept()
            }
        }
    }
}