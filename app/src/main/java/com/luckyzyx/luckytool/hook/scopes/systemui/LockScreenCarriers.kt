package com.luckyzyx.luckytool.hook.scopes.systemui

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.view.isVisible
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.A14
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK

@Obfuscate
object LockScreenCarriers : YukiBaseHooker() {
    override fun onHook() {
        if (SDK >= A14) loadHooker(LockScreenCarrier)
        else loadHooker(LockScreenCarrierV13)
    }

    @Obfuscate
    private object LockScreenCarrier : YukiBaseHooker() {
        override fun onHook() {
            val isRemove = prefs(ModulePrefs).getBoolean("remove_statusbar_carriers", false)
            val customText =
                prefs(ModulePrefs).getString("statusbar_custom_carrier_display_text", "")
            val userFont =
                prefs(ModulePrefs).getBoolean("statusbar_carriers_use_user_typeface", false)


            //Source OplusCarrierTextCallbackInfo
            "com.oplus.systemui.qs.OplusQSCarrierTextController\$OplusCarrierTextCallbackInfo".toClass()
                .apply {
                    constructor().hookAll {
                        after {
                            if (customText.isNotBlank()) {
                                field { name = "carrierText" }.get(instance).set(customText)
                            }
                        }
                    }
                }

            //Source OplusStatCarrierTextController
            "com.oplus.systemui.statusbar.widget.OplusStatCarrierTextController".toClass()
                .apply {
                    method { name = "onViewAttached" }.hook {
                        after {
                            method { name = "setVisible" }.get(instance).call(false)
                        }
                    }
                    method { name = "setVisible" }.hook {
                        before {
                            if (isRemove) args().first().setFalse()
                        }
                    }
                    method { name = "updateCarrierInfo" }.hook {
                        after {
                            if (isRemove) method { name = "setVisible" }.get(instance).call(false)
                        }
                    }
                }

            //Source OplusStatCarrierText
            "com.oplus.systemui.statusbar.widget.OplusStatCarrierText".toClass().apply {
                constructor { paramCount = 2 }.hook {
                    after {
                        if (userFont) instance<TextView>().typeface = Typeface.DEFAULT_BOLD
                    }
                }
                method { name = "onConfigurationChanged" }.hook {
                    after {
                        if (customText.isNotBlank()) instance<TextView>().text = customText
                        if (userFont) instance<TextView>().typeface = Typeface.DEFAULT_BOLD
                    }
                }
            }
        }
    }

    @Obfuscate
    private object LockScreenCarrierV13 : YukiBaseHooker() {
        override fun onHook() {
            val userFont =
                prefs(ModulePrefs).getBoolean("statusbar_carriers_use_user_typeface", false)
            val isRemove = prefs(ModulePrefs).getBoolean("remove_statusbar_carriers", false)
            val customText =
                prefs(ModulePrefs).getString("statusbar_custom_carrier_display_text", "")

            //Source StatOperatorNameView
            "com.oplusos.systemui.statusbar.widget.StatOperatorNameView".toClass().apply {
                constructor { paramCount = 3 }.hook {
                    after {
                        if (userFont) instance<TextView>().typeface = Typeface.DEFAULT_BOLD
                    }
                }
                method { name = "onConfigurationChanged" }.hook {
                    after {
                        if (userFont) instance<TextView>().typeface = Typeface.DEFAULT_BOLD
                    }
                }
                method { name = "updateCarrierInfo";superClass() }.hook {
                    after {
                        instance<TextView>().apply {
                            if (isRemove) isVisible = false
                            if (customText.isNotBlank()) text = customText
                        }
                    }
                }
            }
        }
    }
}