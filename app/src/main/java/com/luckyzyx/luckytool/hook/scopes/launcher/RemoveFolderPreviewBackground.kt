package com.luckyzyx.luckytool.hook.scopes.launcher

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getOSVersionCode

@Obfuscate
object RemoveFolderPreviewBackground : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 34) loadHooker(FolderPreviewBackground)
        else loadHooker(FolderPreviewBackgroundV14)
    }

    @Obfuscate
    object FolderPreviewBackground : YukiBaseHooker() {
        override fun onHook() {
            //Source FolderRoundImageView
            "com.android.launcher3.folder.FolderRoundImageView".toClass().apply {
                method { name = "setImageDrawable" }.hook {
                    before {
                        args().first().setNull()
                    }
                }
            }
        }
    }

    @Obfuscate
    object FolderPreviewBackgroundV14 : YukiBaseHooker() {
        override fun onHook() {
            //Source OplusPreviewBackground folder_icon_bg big_folder_bg
            "com.android.launcher3.folder.OplusPreviewBackground".toClass().apply {
                method { name = "setup" }.hookAll {
                    after {
                        field { name = "mBgDrawable" }.get(instance).setNull()
                    }
                }
                method { name = "drawBackground" }.hook {
                    intercept()
                }
            }
            if (SDK < A13) return
            //Source OplusFolderAnimationManager
            "com.android.launcher3.folder.OplusFolderAnimationManager".toClass().apply {
                method { name = "getFolderBackgroundAnimator" }.hook {
                    intercept()
                }
            }
        }
    }
}