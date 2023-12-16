package com.luckyzyx.luckytool.hook.scope.camera

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getAppSet
import org.luckypray.dexkit.DexKitBridge

class RemoveWatermarkWordLimit(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val appSet = prefs(ModulePrefs).getAppSet(packageName)
        val isNew = "com.oplus.camera.setting.CameraSettingActivity".hasClass()
        val clazz = if (isNew) "$5"
        else when (appSet[2]) {
            "8d5b992", "38e5b1a", "b696b47", "02aac8a" -> "$7"
            else -> "$1"
        }

        //Source CameraSubSettingFragment -> camera_namelength_outofrange -> filter
        //Source CameraSloganSettingFragment -> camera_namelength_outofrange -> filter
        dexKitBridge.findClass {
            searchPackages("com.oplus.camera.setting", "com.oplus.camera.ui.menu.setting")
            matcher {
                fields {
                    addForType(IntType.name)
                    addForType(LongType.name)
                    addForType(BooleanType.name)
                }
                methods {
                    add { name("onDestroy");returnType(UnitType) }
                    add { name("onPause");returnType(UnitType) }
                    add { name("onPreferenceChange");returnType(BooleanType) }
                    add { name("onPreferenceClick");returnType(BooleanType) }
                    add { paramTypes(BundleClass);returnType(UnitType) }
                    add { paramTypes(BundleClass);returnType(BooleanType) }
                    add { paramTypes(StringClass);returnType(UnitType) }
                }
                if (isNew) usingStrings("CameraSubSettingFragment")
                else usingStrings(
                    "CameraSloganSettingFragment",
                    "isSloganEnable",
                    "isVideoSloganEnable"
                )
            }
        }.apply {
            checkDataList("RemoveWatermarkWordLimit")
            (single().name + clazz).toClass().apply {
                method { name = "filter";returnType = CharSequenceClass }.hook {
                    intercept()
                }
            }
        }
    }
}



