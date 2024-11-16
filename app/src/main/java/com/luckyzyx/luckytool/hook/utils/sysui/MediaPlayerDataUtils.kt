package com.luckyzyx.luckytool.hook.utils.sysui

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.hook.scopes.systemui.MediaPlayerPanel.toClass

@Obfuscate
@Suppress("unused")
class MediaPlayerDataUtils(val classLoader: ClassLoader?) {
    val clazz = VariousClass(
        "com.oplus.systemui.qs.media.OplusQsMediaCarouselController\$MediaPlayerData",  //C13.0 C13.1
        "com.oplusos.systemui.media.OplusMediaControllerImpl\$MediaPlayerData",  //C13.2
        "com.oplus.systemui.media.OplusMediaControllerImpl\$MediaPlayerData"  //C14
    ).toClass(classLoader)

    private fun getMediaPlayerData(): Any? {
        return clazz.field { name = "INSTANCE" }.get().any()
    }

    private fun getFirstActiveMediaSortKey(mediaPlayerData: Any?): Any? {
        if (mediaPlayerData == null) return null
        val isSortKey = mediaPlayerData.javaClass.hasMethod { name = "getFirstActiveMediaSortKey" }
        return mediaPlayerData.current().method {
            name = if (isSortKey) "getFirstActiveMediaSortKey" else "firstActiveMedia"
        }.call()
    }

    private fun getMediaData(mediaPlayerData: Any?, firstActiveMediaSortKey: Any?): Any? {
        if (mediaPlayerData == null || firstActiveMediaSortKey == null) return null
        val isGetMediaKey = mediaPlayerData.javaClass.hasMethod { name = "getMediaDataKey" }
        if (isGetMediaKey) mediaPlayerData.current().method {
            name = "getMediaDataKey";paramCount = 1
        }.call(firstActiveMediaSortKey) ?: return null
        val getData = firstActiveMediaSortKey.current().method {
            name = "getData";emptyParam()
        }.call()
        return getData
    }

    fun checkMediaDataStatus(): Any? {
        val mediaPlayerData = getMediaPlayerData()
        val firstActiveMediaKey = getFirstActiveMediaSortKey(mediaPlayerData)
        return getMediaData(mediaPlayerData, firstActiveMediaKey)
    }
}