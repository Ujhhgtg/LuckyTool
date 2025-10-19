package com.luckyzyx.luckytool.hook.scopes.launcher

import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AllowAppNamesDisplayMultipleLines : YukiBaseHooker() {

    val textLineHeight = prefs(ModulePrefs).getInt("custom_app_icon_name_line_height", -1)

    override fun onHook() {
        val osCode = getOSVersionCode

        //Source OplusBubbleTextView
        "com.android.launcher3.OplusBubbleTextView".toClass().resolve().apply {
            if (osCode < 26) {
                firstMethod {
                    name = "setMaxLines"
                    parameters(Int::class)
                }.hook {
                    before {
                        instance<TextView>().maxLines = 2
                        resultNull()
                    }
                }
            }
            if (textLineHeight > -1) {
                firstConstructor { parameterCount = 3 }.hook {
                    after {
                        instance<TextView>().apply {
                            lineHeight = textLineHeight.dp
                        }
                    }
                }
            }
        }
    }
}