package com.luckyzyx.luckytool.hook.scopes.camera

import android.app.Activity
import androidx.preference.PreferenceManager
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.LongArrayType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge
import java.util.concurrent.ExecutorService

class EnableCameraDebugUIOption(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        //Source ConfigSetUtils
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addFieldForType(ExecutorService::class.java)
                    addMethod {
                        paramCount(0)
                        returnType(BooleanType)
                    }
                }
                paramCount(0)
                returnType(BooleanType)
                usingStrings("iq_config_set", "hal_config_set")
            }
        }.apply {
            checkDataList("EnableCameraDebugUIOption ConfigSet")
            single().className.toClass().apply {
                method {
                    name = single().methodName
                    emptyParam()
                    returnType = BooleanType
                }.hook {
                    replaceToTrue()
                }
            }
        }

        //Source NetworkAuthenticationUtils
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addFieldForType(ContextClass)
                    addFieldForType(LongArrayType)
                    addMethod {
                        paramTypes(LongType)
                        returnType(BooleanType)
                    }
                    addMethod {
                        paramTypes(StringClass)
                        returnType(BooleanType)
                    }
                }
                paramTypes(LongType)
                returnType(BooleanType)
                usingNumbers(3600000)
                usingStrings("NetworkAuthenticationUtils")
            }
        }.apply {
            checkDataList("EnableCameraDebugUIOption NetworkAuthentication")
            single().className.toClass().apply {
                method {
                    name = single().methodName
                    param(LongType)
                    returnType = BooleanType
                }.hook {
                    replaceToFalse()
                }
            }
        }

        //Source CameraDebugActivity
        "com.oplus.camera.setting.CameraDebugActivity".toClass().apply {
            method { name = "onCreate" }.hook {
                before {
                    val activity = instance<Activity>()
                    val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                    sp.edit().putBoolean("key_has_checked_auth_connection", true).commit()
                }
            }
        }
    }
}