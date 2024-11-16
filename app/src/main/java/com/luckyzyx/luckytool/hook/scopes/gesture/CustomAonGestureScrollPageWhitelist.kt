package com.luckyzyx.luckytool.hook.scopes.gesture

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ArrayMapClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.ArrayListClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.ListClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class CustomAonGestureScrollPageWhitelist(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val pageSet = prefs(ModulePrefs).getStringSet(
            "custom_aon_gesture_scroll_page_whitelist_list", ArraySet()
        )
//        val videoSet = prefs(ModulePrefs).getStringSet(
//            "custom_aon_gesture_video_whitelist_list", ArraySet()
//        )
        if (pageSet.isEmpty()) return

        //Source ConfigDataUtils
        //Search com.ss.android.ugc.aweme / com.smile.gifmaker
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ContextClass)
                    addForType(ArrayListClass)
                    addForType(ArrayMapClass)
                    addForType(IntType)
                    addForType(FloatType)
                    addForType(ListClass)
                }
                methods {
                    add { paramTypes(StringClass);returnType(IntType) }
                    add { paramTypes(ListClass);returnType(UnitType) }
                }
                usingStrings("com.ss.android.ugc.aweme", "com.smile.gifmaker")
            }
        }.apply {
            checkDataList("CustomAonGestureScrollPageWhitelist")
            single().name.toClass().apply {
                method { emptyParam();returnType = ListClass }.hookAll {
                    after {
                        val res = result<List<String>>() ?: return@after
                        if (res.isEmpty()) return@after
                        result = res.toMutableList().apply {
                            if (contains("com.ss.android.ugc.aweme") || contains("com.smile.gifmaker")) {
                                addAll(pageSet)
                            }
                        }
                    }
                }
            }
        }

        //Source GestureUtil
        "com.oplus.gesture.util.GestureUtil".toClass().apply {
            method { name = "getLocalAonAppListTurnPage" }.hook {
                after {
                    val list = result<List<String>>() ?: return@after
                    result = list.toMutableList().apply {
                        addAll(pageSet)
                    }
                }
            }
//            injectMember {
//                method { name = "getLocalAonAppListPauseOrPlay" }
//                after {
//                    if (videoList.isBlank() || videoList == "None") return@after
//                    val list = result<List<String>>() ?: return@after
//                    result = list.toMutableList().apply {
//                        if (videoList.contains("\n")) {
//                            videoList.replace(" ", "").split("\n")
//                                .forEach { if (it.isNotBlank()) add(it) }
//                        } else add(videoList)
//                    }
//                }
//            }
        }
    }
}