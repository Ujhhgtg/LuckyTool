package com.luckyzyx.luckytool.hook.scope.oplusgames

import android.media.AudioManager
import android.media.SoundPool
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.SparseIntArrayClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.luckyzyx.luckytool.utils.DexkitUtils.checkDataList
import org.luckypray.dexkit.DexKitBridge

class CompetitionModeSound(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    val key = "remove_competition_mode_sound"
    override fun onHook() {
        //Source SoundPoolPlayManager -> competition_mode_sound
        dexKitBridge.findClass {
            matcher {
                fields {
                    addForType(ContextClass.name)
                    addForType(BooleanType.name)
                    addForType(SoundPool::class.java.name)
                    addForType(AudioManager::class.java.name)
                    addForType(SparseIntArrayClass.name)
                }
                methods {
                    add {
                        paramCount(0)
                        returnType(UnitType)
                    }
                    add {
                        paramTypes(IntType)
                        returnType(UnitType)
                    }
                }
            }
        }.apply {
            checkDataList("CompetitionModeSound")
            single().name.toClass().apply {
                method { param(IntType) }.hookAll {
                    before { if (args().first().int() == 9) resultNull() }
                }
            }
        }
    }
}