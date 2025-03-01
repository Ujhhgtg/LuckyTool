package com.luckyzyx.luckytool.hook.scopes.audioeffectcenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import org.lsposed.lsparanoid.Obfuscate
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Obfuscate
object FixRecordCallsOnThirdPartyAppsError : YukiBaseHooker() {
    private const val SpatializerDefine = "com.oplus.audio.effectcenter.manager.SpatializerDefine"
    override fun onHook() {
        //Source com.oplus.audio.effectcenter.manager.SpatializerManager
        "com.oplus.audio.effectcenter.manager.SpatializerManager".toClassOrNull()?.apply {
            method { name = "setSpkVolParam" }.hook {
                before {
                    val level = args().first().int()
                    val mSpatializerMode = field {
                        name = "mSpatializerMode"
                    }.get(instance).cast<AtomicBoolean>() ?: return@before
                    val mSpatializerSpkVol = field {
                        name = "mSpatializerSpkVol"
                    }.get(instance).cast<AtomicInteger>() ?: return@before
                    val mSpatDeviceManager = field {
                        name = "mSpatDeviceManager"
                    }.get(instance).any() ?: return@before
                    val getDeviceForMusicStream = mSpatDeviceManager.current().method {
                        name = "getDeviceForMusicStream"
                    }.invoke<Int>() ?: return@before
                    if (level == mSpatializerSpkVol.get()) return@before
                    if (getDeviceForMusicStream != 2 && mSpatializerMode.get()) return@before
                    val index = SpatializerDefine.toClass().field {
                        name = "PARAM_SET_SPAT_VOLUME_INDEX"
                    }.get().int()
                    method { name = "setParameterImp";paramCount = 3 }.get(instance).call(
                        index, level, mSpatializerSpkVol.get()
                    )
                    resultNull()
                }
            }
        }
    }
}