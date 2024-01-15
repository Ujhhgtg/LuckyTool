package com.luckyzyx.luckytool.utils

import android.util.Log
import com.luckyzyx.luckytool.BuildConfig

@Suppress("MemberVisibilityCanBePrivate")
object LogUtils {
    const val globalTag = "LuckyTool"
    var enable = BuildConfig.DEBUG

    fun d(method: String, params: String, msg: String, send: Boolean = enable) {
        if (send) Log.d(globalTag, "$method: $params -> $msg")
    }

    fun e(method: String, params: String, msg: String, send: Boolean = enable) {
        if (send) Log.e(globalTag, "$method: $params -> $msg")
    }

    fun i(method: String, params: String, msg: String, send: Boolean = enable) {
        if (send) Log.i(globalTag, "$method: $params -> $msg")
    }

    fun v(method: String, params: String, msg: String, send: Boolean = enable) {
        if (send) Log.v(globalTag, "$method: $params -> $msg")
    }

    fun w(method: String, params: String, msg: String, send: Boolean = enable) {
        if (send) Log.w(globalTag, "$method: $params -> $msg")
    }
}