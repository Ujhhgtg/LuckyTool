package com.luckyzyx.luckytool.hook.scopes.audiomonitor

import android.content.Context
import androidx.collection.arrayMapOf
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.ArrayListClass
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringArrayClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.GlobalKeyValue.dyPackName
import com.luckyzyx.luckytool.utils.GlobalKeyValue.qqPackName
import com.luckyzyx.luckytool.utils.GlobalKeyValue.timPackName
import com.luckyzyx.luckytool.utils.GlobalKeyValue.wxPackName
import org.luckypray.dexkit.DexKitBridge

class VoipRecorderWhitelist(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {

    private val audioApplication = "com.oplus.audiomonitor.AudioApplication"
    private val oplusVoipRecorderService =
        "com.oplus.audiomonitor.voiprecord.OplusVoipRecorderService"
    private var switchAppClazz = ""
    private val filePrefix = "未知应用录音"

    val apps = arrayMapOf(
        qqPackName to "QQ",
        wxPackName to "微信",
        timPackName to "Tim",
        dyPackName to "抖音",
//        "org.telegram.messenger" to "Telegram"
    )

    override fun onHook() {
        //Source OplusVoipRecorderService
        oplusVoipRecorderService.toClass(initialize = true).apply {
            method { name = "onCreate" }.hook {
                before {
                    field { type = ArrayListClass }.giveAll().forEachIndexed { _, field ->
                        if ((field.get(null) as List<*>).contains(qqPackName).not())
                            return@forEachIndexed
                        field { name = field.name;type = ArrayListClass }.get()
                            .set(apps.keys.toList())
                    }
                }
            }
            method { emptyParam();returnType = BooleanType }.hook {
                after {
                    val packName = field { type = StringClass }.get().string()
                    if (apps.keys.contains(packName)) resultTrue()
                }
            }
        }

        //Source OplusRecordWrapper
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ContextClass)
                    addForType(StringClass)
                    addForType(BooleanType)
                }
                methods {
                    add { paramCount(0);returnType(UnitType) }
                    add { paramCount(0);returnType(StringClass) }
                    add { paramTypes(StringClass);returnType(BooleanType) }
                }
                usingStrings("OplusRecordWrapper")
            }
        }.apply {
            checkDataList("HookVoipRecorder RecordingFilePrefix")
            single().name.toClass().apply {
                method { emptyParam();returnType = StringClass }.hookAll {
                    after {
                        val fileName = result<String>() ?: return@after
                        val packName = oplusVoipRecorderService.toClass().field {
                            type = StringClass
                        }.get().string()
                        val appName = apps[packName] ?: return@after
                        result = fileName.replace(filePrefix, appName)
                    }
                }
            }
        }

        //Source SwitchApp
        dexKitBridge.findClass {
            matcher {
                addFieldForType(StringClass)
                addFieldForType(BooleanType)
                addMethod { name("equals") }
                addMethod { name("hashCode") }
                addMethod {
                    name("toString")
                    usingStrings("appName", "packageName", "check")
                }
                usingStrings("appName", "packageName", "check")
            }
        }.apply {
            checkDataList("HookVoipRecorder SwitchApp Instance", isDebug = true)
            switchAppClazz = single().name
            if (switchAppClazz.isBlank()) return
        }

        //Source Util
        dexKitBridge.findClass {
            matcher {
                addFieldForType(StringArrayClass)
                addMethod {
                    paramTypes(ListClass)
                    returnType(ListClass)
                    usingStrings(qqPackName, wxPackName, "enable_record_app")
                }
                addMethod {
                    paramTypes(StringClass)
                    returnType(BooleanClass)
                }
            }
        }.apply {
            checkDataList("HookVoipRecorder Util", isDebug = true)
            single().name.toClass().apply {
                method { param(ListClass);returnType = ListClass }.hook {
                    before {
                        val list = ArrayList<Any>()
                        var prefsValue = ""

                        val context = audioApplication.toClass().field { type = ContextClass }.get()
                            .cast<Context>() ?: return@before
                        val prefs = context.getSharedPreferences(
                            context.packageName + "_preferences", Context.MODE_PRIVATE
                        )

                        val enabledApp = prefs.getString("enable_record_app", "")?.split("#")
                            ?: arrayListOf()

                        apps.onEachIndexed { index, entry ->
                            val app = switchAppClazz.toClass().buildOf(
                                entry.key, enabledApp.contains(entry.key)
                            ) { param(StringClass, BooleanType) }?.apply {
                                current().field { type = StringClass;order().index().last() }
                                    .set(entry.value)
                            } ?: return@onEachIndexed
                            list.add(app)

                            if (enabledApp.contains(entry.key)) {
                                if (index > 0) prefsValue += "#"
                                prefsValue += entry.key
                            }
                        }

                        if (prefsValue.isBlank()) return@before
                        prefs.edit().putString("enable_record_app", prefsValue).apply()
                        result = list
                    }
                }
            }
        }
    }
}