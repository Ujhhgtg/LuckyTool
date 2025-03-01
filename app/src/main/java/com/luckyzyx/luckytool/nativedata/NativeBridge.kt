package com.luckyzyx.luckytool.nativedata

import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.LogUtils

@Obfuscate
object NativeBridge {

    private val libName = "nativedata"
    private var mInitializeLoadLibrary = false

    init {
        try {
            if (!mInitializeLoadLibrary) {
                synchronized(NativeBridge::class.java) {
                    val time = System.currentTimeMillis()
                    System.loadLibrary(libName)
                    val lastTime = System.currentTimeMillis()
                    val costTime = lastTime - time
                    LogUtils.d(
                        "NativeBridge",
                        "loadLibrary",
                        "load native data success, cost $costTime ms",
                        true
                    )
                    mInitializeLoadLibrary = true
                }
            }
        } catch (t: Throwable) {
            LogUtils.d("NativeBridge", "loadLibrary error", t.toString(), true)
            mInitializeLoadLibrary = false
        }
    }

    external fun getString(): String
    external fun getBoolean(): Boolean
    external fun getInt(): Int
    external fun getLong(): Long
    external fun getFloat(): Float
    external fun getList(): List<*>

    external fun getIdList(index: Int): ArrayList<String>

}