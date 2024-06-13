package com.luckyzyx.luckytool.hook.scopes.otherapp

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.SharedPreferences
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ActivityClass
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.android.MediaPlayerClass
import com.highcapable.yukihookapi.hook.type.android.PendingIntentClass
import com.highcapable.yukihookapi.hook.type.android.SharedPreferencesClass
import com.highcapable.yukihookapi.hook.type.android.TypefaceClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.HashMapClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs

object HookADM : YukiBaseHooker() {
    override fun onHook() {
        val isPro = prefs(ModulePrefs).getBoolean("adm_unlock_pro", false)
        if (!isPro) return
        //Search menu_buy -> firebase.test.lab
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            dexKitBridge.findClass {
                matcher {
                    fields {
                        addForType(SharedPreferencesClass)
                        addForType(SharedPreferences.Editor::class.java)
                        addForType(HandlerClass)
                        addForType(HashMapClass)
                        addForType(TypefaceClass)
                        addForType(ClipboardManager::class.java)
                        addForType(AlarmManager::class.java)
                        addForType(NotificationManager::class.java)
                        addForType(MediaPlayerClass)
                        addForType(PendingIntentClass)
                    }
                    usingStrings("firebase.test.lab")
                }
            }.apply {
                checkDataList("HookADM")
                single().name.toClass().apply {
                    method {
                        modifiers { isStatic }
                        param(ActivityClass)
                        returnType = UnitType
                    }.hookAll {
                        after {
                            field {
                                name = "n"
                                modifiers { isStatic }
                                type(BooleanType)
                            }.get().setTrue()
                        }
                    }
                }
            }
        }
    }
}