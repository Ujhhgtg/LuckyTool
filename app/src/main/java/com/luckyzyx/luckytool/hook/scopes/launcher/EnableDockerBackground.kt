package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object EnableDockerBackground : YukiBaseHooker() {
    override fun onHook() {
        //Source ScreenUtils
        "com.android.common.util.ScreenUtils".toClass().resolve().apply {
            firstMethod { name = "isSupportDockerExpandScreen" }.hook {
                replaceToTrue()
            }
            //OplusTaskHeaderView showSplitWindowIcon
//            firstMethod { name = "hasLargeDisplayFeatures" }.hook {
//                replaceToTrue()
//            }
        }
    }
}