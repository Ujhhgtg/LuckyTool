package com.luckyzyx.luckytool.service.controller

import android.content.Intent
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.luckyzyx.luckytool.IDarkModeController
import com.luckyzyx.luckytool.hook.utils.IColorDisplayUtils
import com.topjohnwu.superuser.ipc.RootService

class DarkModeControllerService : RootService() {
    companion object {

        private val iColorDisplayManagerInternal by lazy {
            IColorDisplayUtils(null).getInstance()
        }
    }

    override fun onBind(intent: Intent) = object : IDarkModeController.Stub() {

        override fun checkDarkMode(): Boolean {
            return iColorDisplayManagerInternal != null
        }

        override fun getDarkMode(): Boolean {
            return try {
                iColorDisplayManagerInternal?.current()?.method {
                    name = "isReduceBrightColorsActivated"
                    emptyParam()
                }?.boolean() ?: false
            } catch (_: Throwable) {
                false
            }
        }

        override fun setDarkMode(status: Boolean) {
            try {
                iColorDisplayManagerInternal?.current()?.method {
                    name = "setReduceBrightColorsActivated"
                    param(BooleanType)
                }?.call(status)
            } catch (_: Throwable) {

            }
        }
    }
}