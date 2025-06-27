package com.luckyzyx.luckytool.hook.scopes.camera

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import java.util.concurrent.ExecutorService

@Obfuscate
class EnableCameraDebugUIOption(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source ConfigSetUtils
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addFieldForType(ExecutorService::class.java)
                    addMethod {
                        paramCount(0)
                        returnType(Boolean::class.java)
                    }
                }
                paramCount(0)
                returnType(Boolean::class.java)
                usingStrings("iq_config_set", "hal_config_set")
            }
        }.apply {
            checkDataList("EnableCameraDebugUIOption ConfigSet")
            single().className.toClass().resolve().apply {
                firstMethod {
                    name = single().methodName
                    emptyParameters()
                    returnType = Boolean::class
                }.hook {
                    replaceToTrue()
                }
            }
        }

        //Source NetworkAuthenticationUtils
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addFieldForType(Context::class.java)
                    addFieldForType(LongArray::class.java)
                    LongArray::class
                    addMethod {
                        paramTypes(Long::class.java)
                        returnType(Boolean::class.java)
                    }
                    addMethod {
                        paramTypes(String::class.java)
                        returnType(Boolean::class.java)
                    }
                }
                paramTypes(Long::class.java)
                returnType(Boolean::class.java)
                usingNumbers(3600000)
                usingStrings("NetworkAuthenticationUtils")
            }
        }.apply {
            checkDataList("EnableCameraDebugUIOption NetworkAuthentication")
            single().className.toClass().resolve().apply {
                firstMethod {
                    name = single().methodName
                    parameters(Long::class)
                    returnType = Boolean::class
                }.hook {
                    replaceToFalse()
                }
            }
        }

        //Source CameraDebugActivity
        "com.oplus.camera.setting.CameraDebugActivity".toClass().resolve().apply {
            firstMethod { name = "onCreate" }.hook {
                before {
                    val activity = instance<Activity>()
                    val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                    sp.edit(commit = true) { putBoolean("key_has_checked_auth_connection", true) }
                }
            }
        }
    }
}