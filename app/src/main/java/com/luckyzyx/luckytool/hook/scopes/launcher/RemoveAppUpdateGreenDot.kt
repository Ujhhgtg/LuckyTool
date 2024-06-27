package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method

object RemoveAppUpdateGreenDot : YukiBaseHooker() {
    override fun onHook() {
        //Source BubbleTextView
        "com.android.launcher3.BubbleTextView".toClass().apply {
            method { name = "isShouldShowGreenDot" }.hook {
                replaceToFalse()
            }
        }
    }
}