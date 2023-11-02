@file:Suppress("unused")

package com.luckyzyx.luckytool.utils

import android.app.Application
import android.content.Context
import android.util.ArrayMap
import com.drake.net.Get
import com.drake.net.utils.scope
import com.drake.net.utils.scopeNet
import com.drake.net.utils.withDefault
import com.drake.net.utils.withIO
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.BuildConfig
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

@Obfuscate
object AppAnalyticsUtils {

    private const val App_Center_Secret = BuildConfig.APP_CENTER_SECRET

    fun init(instance: Application, isBeta: Boolean) {
        if (App_Center_Secret.isNotBlank()) {
            if (isBeta) AppCenter.start(instance, App_Center_Secret, Analytics::class.java)
            else AppCenter.start(
                instance, App_Center_Secret,
                Analytics::class.java, Crashes::class.java
            )
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun trackEvent(name: String, data: Map<String, String>? = null) {
        if (data != null) Analytics.trackEvent(name, data)
        else Analytics.trackEvent(name)
    }

    fun Context.ckqcEbk(): Boolean {
        var status = false
        scopeNet {
            val latestUrl = "https://gitee.com/luckyzyx/luckyzyx/raw/master/ebk.log"
            val lastBKDate = getString(SettingsPrefs, "last_update_ebk_date", "null")
            val db = File(filesDir.path + "/ebk")
            val getDoc = Get<String>(latestUrl).await()
            val list = getDoc.split("\n")
            val json = list[1]
            if (list[0] != lastBKDate) {
                val command = arrayOf(
                    "chattr -i ${db.absolutePath}",
                    "chattr -i /data/local/tmp/ebk",
                    "echo $json > ${db.absolutePath}",
                    "echo $json > /data/local/tmp/ebk",
                    "chattr +i ${db.absolutePath}",
                    "chattr +i /data/local/tmp/ebk"
                )
                withDefault { ShellUtils.execCommand(command, true) }
                putString(SettingsPrefs, "last_update_ebk_date", list[0])
                status = true
            }
        }.catch {
            status = false
            LogUtils.e("ckqcEbk", "throw", "$it")
            return@catch
        }.finally { scope { withIO { ckqcbs("ebk") } } }
        return status
    }

    fun Context.ckqcBBK(): Boolean {
        var status = false
        val db = File(filesDir.path + "/bbk")
        scopeNet {
            val latestUrl =
                "https://api.github.com/repos/luckyzyx/LuckyTool_Doc/releases/tags/ltbks"
            val lastBKDate = getString(SettingsPrefs, "last_update_bbk_date", "null")
            val getDoc = Get<String>(latestUrl).await()
            JSONObject(getDoc).apply {
                val date = optString("name").takeIf { e -> e.isNotBlank() } ?: return@scopeNet
                val json = optString("body").takeIf { e -> e.isNotBlank() } ?: return@scopeNet
                if (date != lastBKDate) {
                    val command = arrayOf(
                        "chattr -i ${db.absolutePath}",
                        "chattr -i /data/local/tmp/bbk",
                        "echo $json > ${db.absolutePath}",
                        "echo $json > /data/local/tmp/bbk",
                        "chattr +i ${db.absolutePath}",
                        "chattr +i /data/local/tmp/bbk"
                    )
                    withDefault { ShellUtils.execCommand(command, true) }
                    putString(SettingsPrefs, "last_update_bbk_date", date)
                    status = true
                }
            }
        }.catch {
            status = false
            LogUtils.e("ckqcBBK", "throw", "$it")
            val command = arrayOf(
                "chattr -i ${db.absolutePath}",
                "chattr -i /data/local/tmp/bbk",
                "rm ${db.absolutePath}",
                "rm /data/local/tmp/bbk",
            )
            scope { withDefault { ShellUtils.execCommand(command, true) } }
            return@catch
        }.finally { scope { withIO { ckqcbs("bbk") } } }
        return status
    }

    fun Context.ckqcbs(name: String): Boolean {
        scope {
            withDefault {
                var qbsval = false
                var cbsval = false
                var disval = false
                val map = ArrayMap<String, String>()
                map["time"] = formatDate("YYYYMMdd-HH:mm:ss")
                val db = File(filesDir.path + "/$name")
                val db2 = File("/data/local/tmp/$name")
                if (!db.exists() && !db2.exists()) return@withDefault
                val qss = getQSlist()
                val css = getCSid()
                val gid = getGuid
                val bks = db.readText().let { it.substring(1, it.length) }
                val bks2 = db2.readText().let { it.substring(1, it.length) }
                try {
                    val js = JSONObject(base64Decode(bks).replace("\\\"", "\""))
                    (js.optJSONArray("qbk") ?: JSONArray()).apply {
                        qss.forEach {
                            if (this.toString().contains("\"$it\"")) {
                                qbsval = true
                                map["qbk"] = it
                            }
                        }
                    }
                    (js.optJSONArray("cbk") ?: JSONArray()).apply {
                        css.forEach {
                            if (this.toString().contains("\"$it\"")) {
                                cbsval = true
                                map["cbk"] = it
                            }
                        }
                    }
                    (js.optJSONArray("dik") ?: JSONArray()).apply {
                        if (this.toString().contains("\"$gid\"")) {
                            disval = true
                            map["dik"] = gid
                        }
                    }
                } catch (e: Exception) {
                    LogUtils.e("ckqcbs", "search ebk", "$e")
                }
                if (bks.length != bks2.length) try {
                    val js2 = JSONObject(base64Decode(bks2).replace("\\\"", "\""))
                    (js2.optJSONArray("qbk") ?: JSONArray()).apply {
                        qss.forEach {
                            if (this.toString().contains("\"$it\"")) {
                                qbsval = true
                                map["2qbk"] = it
                            }
                        }
                    }
                    (js2.optJSONArray("cbk") ?: JSONArray()).apply {
                        css.forEach {
                            if (this.toString().contains("\"$it\"")) {
                                cbsval = true
                                map["2cbk"] = it
                            }
                        }
                    }
                    (js2.optJSONArray("dik") ?: JSONArray()).apply {
                        if (this.toString().contains("\"$gid\"")) {
                            disval = true
                            map["2dik"] = gid
                        }
                    }
                } catch (e: Exception) {
                    LogUtils.e("ckqcbs", "search bbk", "$e")
                }
                if (qbsval || cbsval || disval) {
                    trackEvent("bk", map)
                    removeModule()
                    exitModule()
                }
            }
        }.catch {
            LogUtils.e("ckqcbs", "throw", "$it")
            return@catch
        }
        return true
    }
}