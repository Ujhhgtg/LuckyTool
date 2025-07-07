package com.luckyzyx.luckytool.hook.utils.sysui

import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class MediaPlayerDataUtils(val classLoader: ClassLoader?) {
    val clazz = VariousClass(
        "com.oplus.systemui.qs.media.OplusQsMediaCarouselController\$MediaPlayerData",  //C13.0 C13.1
        "com.oplusos.systemui.media.OplusMediaControllerImpl\$MediaPlayerData",  //C13.2
        "com.oplus.systemui.media.OplusMediaControllerImpl\$MediaPlayerData"  //C14
    ).load(classLoader)

    private fun getMediaPlayerData(): Any? {
        return clazz.resolve().firstField { name = "INSTANCE" }.get()
    }

    private fun getFirstActiveMediaSortKey(mediaPlayerData: Any?): Any? {
        return mediaPlayerData?.asResolver()?.let {
            (it.firstMethodOrNull { name = "getFirstActiveMediaSortKey" }
                ?: it.firstMethod { name = "firstActiveMedia" }).invoke()
        }
    }

    private fun getMediaData(mediaPlayerData: Any?, firstActiveMediaSortKey: Any?): Any? {
        mediaPlayerData?.asResolver()?.firstMethodOrNull {
            name = "getMediaDataKey";parameterCount = 1
        }?.invoke(firstActiveMediaSortKey) ?: return null
        val getData = firstActiveMediaSortKey?.asResolver()?.firstMethod {
            name = "getData";emptyParameters()
        }?.invoke()
        return getData
    }

    fun checkMediaDataStatus(): Any? {
        val mediaPlayerData = getMediaPlayerData()
        val firstActiveMediaKey = getFirstActiveMediaSortKey(mediaPlayerData)
        return getMediaData(mediaPlayerData, firstActiveMediaKey)
    }
}