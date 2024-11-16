package com.luckyzyx.luckytool.hook.scopes.otherapp

import android.app.Activity
import androidx.preference.PreferenceManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.query.enums.StringMatchType

@Obfuscate
object HookADM : YukiBaseHooker() {
    override fun onHook() {
        //解锁Pro
        if (prefs(ModulePrefs).getBoolean("adm_unlock_pro", false)) {
            loadHooker(UnlockAdmPro)
        }
        //解锁线程数
        loadHooker(UnlockAdmThreads)
    }

    object UnlockAdmPro : YukiBaseHooker() {
        override fun onHook() {
            //Search Beta / Pro -> EVENT_DISA / hua_voices
            "com.dv.get.Main".toClass().apply {
                method { name = "onCreate" }.hook {
                    after {
                        val activity = instance<Activity>()
                        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                        sp.edit().apply {
                            putBoolean("EVENT_DISA", false)
                            putBoolean("hua_voices", false)
                        }.commit()
                    }
                }
            }
        }
    }

    @Obfuscate
    object UnlockAdmThreads : YukiBaseHooker() {
        override fun onHook() {
            val threads = prefs(ModulePrefs).getString("adm_unlock_more_threads", "0")
                .toIntOrNull() ?: 0
            if (threads <= 0) return

            //Source Main -> S215
            DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
                dexKitBridge.findClass {
                    matcher {
                        addFieldForType(IntType)
                        methods {
                            add {
                                name("call", StringMatchType.Contains)
                                paramCount(0);returnType(IntType)
                                usingNumbers(15)
                            }
                            add {
                                name("call", StringMatchType.Contains)
                                paramCount(0);returnType(BooleanType)
                            }
                        }
                    }
                }.apply {
                    checkDataList("UnlockAdmThreads", isDebug = true)
                    single().name.toClass().apply {
                        method {
                            name { it.contains("call") }
                            emptyParam()
                            returnType = IntType
                        }.hook {
                            after {
//                                val type = field { type = IntType }.get(instance).int()
                                val res = result<Int>() ?: return@after
                                if (res == 15) result = threads - 1
                            }
                        }
                    }
                }
            }
        }
    }
}