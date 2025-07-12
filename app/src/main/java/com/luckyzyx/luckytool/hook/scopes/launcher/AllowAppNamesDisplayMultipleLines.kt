package com.luckyzyx.luckytool.hook.scopes.launcher

import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AllowAppNamesDisplayMultipleLines : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusBubbleTextView
        "com.android.launcher3.OplusBubbleTextView".toClass().resolve().apply {
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
    }
}