package com.luckyzyx.luckytool.hook.scopes.launcher

import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object AllowAppNamesDisplayMultipleLines : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusBubbleTextView
        "com.android.launcher3.OplusBubbleTextView".toClass().apply {
            method { name = "setTextVisibility";paramCount = 1 }.hook{
                before {
                    instance<TextView>().isSingleLine = false
                }
            }
        }
    }
}