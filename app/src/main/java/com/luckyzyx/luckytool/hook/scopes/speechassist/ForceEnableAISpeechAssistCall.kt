package com.luckyzyx.luckytool.hook.scopes.speechassist

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.joom.paranoid.Obfuscate

@Obfuscate
object ForceEnableAISpeechAssistCall : YukiBaseHooker() {
    override fun onHook() {
        //Source AiCallCommonBean
        "com.heytap.speechassist.aicall.setting.config.AiCallCommonBean".toClass().apply {
            method { name = "getSupportAiCall" }.hook {
                replaceToTrue()
            }
            method { name = "getSupportAiCallV2" }.hook {
                replaceToTrue()
            }
        }
    }
}