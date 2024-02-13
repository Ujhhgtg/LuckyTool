package com.luckyzyx.luckytool.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.ui.activity.MainActivity

@Suppress("PrivatePropertyName")
@Obfuscate
class SecretCodeReceiver : BroadcastReceiver() {
    private val SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == SECRET_CODE_ACTION) {
            val code = intent.data?.host ?: return
            if (code == "582598665") Intent().apply {
                setClass(context, MainActivity::class.java)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                context.startActivity(this)
            }
        }
    }
}