@file:Suppress("unused", "IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")

package com.luckyzyx.luckytool.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.content.pm.PackageManager.DONT_KILL_APP
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.os.SystemProperties
import android.provider.Settings
import android.service.quicksettings.TileService
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.ArrayMap
import android.util.ArraySet
import android.util.Base64
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.android.internal.os.PowerProfile
import com.drake.net.utils.scope
import com.drake.net.utils.withDefault
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.IGlobalFuncController
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.utils.AppAnalyticsUtils.startCheckListFinal
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.Dispatchers
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.regex.Pattern
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.random.Random
import kotlin.system.exitProcess

/***
 * 获取APP Commit
 * @receiver Context
 * @param packName String
 * @return String
 */
@Suppress("DEPRECATION")
fun Context.getAppCommit(packName: String): String? {
    val appInfo = PackageUtils(packageManager).getApplicationInfo(
        packName, PackageManager.GET_META_DATA
    )
    return safeOfNull { appInfo?.metaData?.get("versionCommit").toString() }
}

/**
 * 获取APP版本/版本号/Commit
 * 写入SP xml文件内
 * @return [ArraySet]
 */
@Suppress("DEPRECATION") //修复获取null
fun Context.getAppVersion(packName: String, save: Boolean = true): ArrayList<String> =
    safeOf(ArrayList()) {
        val arrayList = ArrayList<String>()
        val arraySet = ArraySet<String>()
        val packageInfo = PackageUtils(packageManager).getPackageInfo(
            packName, PackageManager.GET_META_DATA
        ) ?: return@safeOf ArrayList()
        val commitData = packageInfo.applicationInfo?.metaData
        val versionName = packageInfo.versionName ?: "null"
        arrayList.add(versionName)
        arraySet.add("name|$versionName")
        val versionCode = packageInfo.longVersionCode.toString()
        arrayList.add(versionCode)
        arraySet.add("code|$versionCode")
        val versionCommit = commitData?.get("versionCommit").toString()
        val versionDate = commitData?.get("versionDate").toString()
        //Fix the camera's commit is empty
        val commit = versionCommit.ifBlank { versionDate.ifBlank { "null" } }
        arrayList.add(commit)
        arraySet.add("commit|$commit")
        if (save) putStringSet(ModulePrefs, packName, arraySet)
        return arrayList
    }

/**
 * 获取APP版本数组
 * @receiver YukiHookPrefsBridge
 * @param packName String
 * @return Array<String>
 */
fun YukiHookPrefsBridge.getAppSet(packName: String): Array<String> {
    val newArray = arrayOf("null", "null", "null")
    getStringSet(packName, ArraySet()).apply {
        if (isEmpty()) return newArray
        forEach {
            if (it.isNullOrEmpty()) return@forEach
            if (it.contains("name|")) newArray[0] = it.substring(5)
            if (it.contains("code|")) newArray[1] = it.substring(5)
            if (it.contains("commit|")) newArray[2] = it.substring(7)
        }
        return newArray
    }
}

/**
 * 获取设备信息
 * @receiver Context
 * @return String
 */
fun Context.getDeviceInfo(
    controller: IGlobalFuncController? = null, isLog: Boolean = false
): String {
    val androidVer = "Android ${Build.VERSION.RELEASE}(${Build.VERSION.SDK_INT})"
    val osVer = "OS $getOSVersionName($getOSVersionCode)"
    return """
        ${getString(R.string.model)}: ${Build.BRAND} ${Build.MODEL}
        ${getString(R.string.market_name)}: ${getModelMarketName()}
        ${getString(R.string.product)}: ${Build.PRODUCT} ${Build.DEVICE}
        ${getString(R.string.system)}: $androidVer $osVer
        ${getString(R.string.build_version)}: ${Build.DISPLAY}
        ${getString(R.string.version)}: ${controller?.otaVersion}
        ${getString(R.string.flash)}: ${controller?.flashInfo}
        LCD: ${controller?.lcdInfo}
        PAS: ${controller?.pcbInfo} ${controller?.snInfo}
        RTD: $getRecruit
    """.trimIndent().let {
        if (isLog) "$it\n${getString(R.string.module_version)} $getVersionName($getVersionCode)\n\n" else it
    }
}

/**
 * 检测包名是否存在
 * @receiver Context
 * @param packName String
 * @return Boolean
 */
fun Context.checkPackName(packName: String): Boolean {
    return PackageUtils(packageManager).getPackageInfo(packName, 0) != null
}

/**
 * 获取APP图标
 * @receiver Context
 * @param packName String
 * @return Drawable?
 */
fun Context.getAppIcon(packName: String): Drawable? = safeOfNull {
    return PackageUtils(packageManager).getApplicationInfo(packName, 0)?.loadIcon(packageManager)
}

/**
 * 获取APP版本名
 * @receiver Context
 * @param packName String
 * @return String?
 */
fun Context.getAppVersionName(packName: String): String? {
    return PackageUtils(packageManager).getPackageInfo(packName, 0)?.versionName
}

/**
 * 获取APP版本号
 * @receiver Context
 * @param packName String
 * @return Long?
 */
fun Context.getAppVersionCode(packName: String): Long? {
    return PackageUtils(packageManager).getPackageInfo(packName, 0)?.longVersionCode
}

/**
 * 获取APP名称
 * @receiver Context
 * @param packName String 包名
 * @return CharSequence?  若为Null 返回包名
 */
fun Context.getAppLabel(packName: String): CharSequence {
    return getAppLabelOrNull(packName) ?: packName
}

/**
 * 获取APP名称
 * @receiver Context
 * @param packName String
 * @return CharSequence?
 */
fun Context.getAppLabelOrNull(packName: String): CharSequence? = safeOfNull {
    return PackageUtils(packageManager).getApplicationInfo(packName, 0)?.loadLabel(packageManager)
}

/**
 * 判断Activity是否存在
 * @receiver Context
 * @param intent Intent
 * @return Boolean
 */
fun Context.checkResolveActivity(intent: Intent): Boolean = safeOfFalse {
    return PackageUtils(packageManager).resolveActivity(intent, 0) != null
}

/**
 * 判断Activity是否存在
 * @receiver Context
 * @param packName String
 * @param className String
 * @return Boolean
 */
fun Context.checkResolveActivity(packName: String, className: String): Boolean = safeOfFalse {
    return checkResolveActivity(Intent().setClassName(packName, className))
}

/**
 * Toast快捷方法
 * @receiver Context
 * @param id Int
 * @param long Boolean?
 */
fun Context.toast(id: Int, long: Boolean? = false) {
    toast(getString(id), long)
}

fun Context.toast(msg: String, long: Boolean? = false) = if (long == true) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
} else {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

/**
 * 获取自定义刷新率
 * @return [List]
 */
fun getFpsMode1(): ArrayList<Any?> {
    return ArrayList<Any?>().apply {
        add(0, DisplayMode(0, null, null, null, null, 30.0F))
        add(1, DisplayMode(1, null, null, null, null, 60.0F))
        add(2, DisplayMode(2, null, null, null, null, 90.0F))
        add(3, DisplayMode(3, null, null, null, null, 120.0F))
        add(4, DisplayMode(4, null, null, null, null, 144.0F))
    }
}

/**
 * 获取设备刷新率
 * @receiver Context
 * @return Array<String>
 */
fun getFpsMode2(): ArrayList<ArrayList<*>> = safeOf(ArrayList()) {
    val command =
        "dumpsys display | grep -A 24 'mSfDisplayModes=' | grep ' DisplayMode{id=' | cut -f2 -d '{' | while read row; do\n" + "  if [[ -n \$row ]]; then\n" + "    echo \$row | tr ',' '\\n' | while read col; do\n" + "      case \$col in\n" + "        'id='*)\n" + "          echo -n \$(echo \${col:3}'|')\n" + "        ;;\n" + "      'width='*)\n" + "        echo -n \$(echo \${col:6})\n" + "        ;;\n" + "      'height='*)\n" + "        echo -n x\$(echo \${col:7})\n" + "        ;;\n" + "      'refreshRate='*)\n" + "        echo ' '\$(echo \${col:12} | cut -f1 -d '.')Hz\n" + "        ;;\n" + "      esac\n" + "    done\n" + "    echo -e '\\\\n'\n" + "  fi\n" + "done"
    var dataArr: ArrayList<String>
    val idArr = ArrayList<Int>()
    val fpsArr = ArrayList<String>()
    ShellUtils.fastCmd(command).apply {
        dataArr = if (isBlank()) ArrayList()
        else split("\\n").toMutableList().apply {
            removeIf { e -> e.isBlank() }
        } as ArrayList<String>
    }
    dataArr.forEach {
        val id = it.split("|").takeIf { e -> e.size >= 2 }?.get(0) ?: return@forEach
        val fps = it.split("|").takeIf { e -> e.size >= 2 }?.get(1) ?: return@forEach
        idArr.add(id.toInt())
        fpsArr.add(fps)
    }
    return ArrayList<ArrayList<*>>().apply {
        idArr.takeIf { e -> e.isNotEmpty() }?.let { add(it) }
        fpsArr.takeIf { e -> e.isNotEmpty() }?.let { add(it) }
    }
}

/**
 * 设置刷新率
 * @param context Context
 * @param refresh String?
 * @param name String
 */
fun setRefresh(context: Context, name: String, refresh: String?) {
    setParameter(context, name, "min_refresh_rate", refresh)
    setParameter(context, name, "peak_refresh_rate", refresh)
}

fun setRefresh(context: Context, name: String, minRefresh: String?, peakRefresh: String?) {
    setParameter(context, name, "min_refresh_rate", minRefresh)
    setParameter(context, name, "peak_refresh_rate", peakRefresh)
}

fun setParameter(context: Context, name: String, key: String?, value: String?) {
    val contentResolver = context.contentResolver
    safeOf({
        context.toast("apply $name Hz failed!")
    }) {
        val contentValues = ContentValues(2)
        contentValues.put("name", key)
        contentValues.put("value", value)
        contentResolver.insert(Uri.parse("content://settings/system"), contentValues)
//        context.toast("apply $name Hz success!")
    }
}

/**
 * 获取机型市场名
 * @return String?
 */
fun getModelMarketName(): String? {
    return SystemProperties.get("ro.vendor.oplus.market.name")
}

/**
 * 获取主板ID
 * @return String
 */
fun getDeviceID(): String {
    val serialNumber = ShellUtils.fastCmd("cat /sys/devices/soc0/serial_number")
    if (serialNumber.isNotBlank()) return serialNumber
    val serialNo = ShellUtils.fastCmd("cat /sys/firmware/devicetree/base/firmware/android/serialno")
    if (serialNo.isNotBlank()) return serialNo
    return "null"
}


/**
 * 获取GUID
 * /data/system/openid_config.xml
 */
val getGuid: String
    get() = ShellUtils.fastCmd("cat /data/system/openid_config.xml | sed  -n '3p'").let {
        if ((it.isNotBlank())) it.split("\"").getOrNull(3) ?: "null"
        else "null"
    }

/**
 * 获取招募ID
 * /data/user/0/com.oplus.ota/shared_prefs/persistent_info.xml
 */
val getRecruit: String
    get() = ShellUtils.fastCmd(
        "cat /data/user/0/com.oplus.ota/shared_prefs/persistent_info.xml | grep ota_register_trigger_id | cut -f2 -d '>' | cut -f1 -d '<'"
    ).let { it.ifBlank { "null" } }

/**
 * 获取prop数据
 * @param key String
 * @return String
 */
fun getProp(key: String): String = ShellUtils.fastCmd("getprop $key").let {
    if (it.isBlank()) "null" else formatSpace(it)
}


/**
 * 打开空活动以关闭折叠面板
 * @receiver TileService
 */
@SuppressLint("StartActivityAndCollapseDeprecated")
@Suppress("DEPRECATION")
fun TileService.closeCollapse() {
    try {
        startActivityAndCollapse(Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    } catch (_: Exception) {
        return
    }
}

/**
 * 跳转工程模式
 * @param context Context
 */
fun jumpEngineermode(context: Context) {
    val packInfo = PackageUtils(context.packageManager).getInstalledPackages(0)
        .find { it.packageName.contains("engineermode") } ?: return
    val packName = packInfo.packageName
    val isMain = context.checkResolveActivity(
        Intent().setClassName(packName, "${packName}.EngineerModeMain")
    )
    val activity = if (isMain) "EngineerModeMain" else "aftersale.AfterSalePage"
    ShellUtils.fastCmd("am start -n ${packName}/.$activity")
}

/**
 * 跳转充电测试
 * @param context Context
 */
fun jumpBatteryInfo(context: Context) {
    val packInfo = PackageUtils(context.packageManager).getInstalledPackages(
        PackageManager.GET_ACTIVITIES
    ).find { it.packageName.contains("engineermode") } ?: return
    val packName = packInfo.packageName
    val activity = packInfo.activities.find {
        it.name.contains("EngineerFragmentContainer")
    }?.name
    val chargeTestClazz = "$packName.aftersale.manualtest.ASChargeTestFragmentCompat"
    if (activity == null) LogUtils.e(
        "jumpBatteryInfo", "activity", "EngineerFragmentContainer is null", true
    ) else ShellUtils.fastCmd("am start -n $packName/$activity -e fragment $chargeTestClazz")
}

/**
 * 跳转到设置开发者选项页面
 * @param context Context
 */
fun jumpSettingsDev(context: Context) {
    try {
        context.startActivity(Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS").apply {
            setPackage("com.android.settings")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        })
    } catch (e: ActivityNotFoundException) {
        ShellUtils.fastCmd("am start -a com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS")
    }
}

/**
 * 跳转到系统界面调节工具
 * @param context Context
 */
fun jumpSystemUIDemoMode(context: Context) {
    try {
        context.startActivity(Intent().apply {
            setPackage("com.android.systemui")
            setClassName("com.android.systemui", "com.android.systemui.DemoMode")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        })
    } catch (e: ActivityNotFoundException) {
        ShellUtils.fastCmd("am start -n com.android.systemui/.DemoMode")
    }
}

/**
 * 跳转应用分身
 * @param context Context
 */
fun jumpMultiApp(context: Context) {
    if (context.checkPackName("com.oplus.multiapp")) {
        ShellUtils.fastCmd("am start com.oplus.multiapp/.ui.entry.ActivityMainActivity")
    }
}

/**
 * 跳转暗色模式
 * @param context Context
 */
fun jumpDarkMode(context: Context) {
    Intent("com.android.settings.DISPLAY_SETTINGS").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        context.startActivity(this)
    }
}

/**
 * 跳转到软件更新
 * @param context Context
 */
fun jumpOTA(context: Context) {
    if (context.checkPackName("com.oplus.ota")) {
        ShellUtils.fastCmd("am start com.oplus.ota/com.oplus.otaui.activity.EntryActivity")
    }
}

/**
 * 跳转到乐划锁屏设置页面
 * @param context Context
 */
fun jumpPictorial(context: Context) {
    if (context.checkPackName("com.heytap.pictorial")) {
        Intent(Intent.ACTION_MAIN).apply {
            setPackage("com.heytap.pictorial")
            setClassName("com.heytap.pictorial", "com.heytap.pictorial.ui.SettingActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            context.startActivity(this)
        }
    }
}

/**
 * 跳转到手势体感页面
 * @param context Context
 */
fun jumpGesture(context: Context) {
    if (context.checkPackName("com.oplus.gesture")) {
        ShellUtils.fastCmd("am start com.oplus.gesture/.guide.GestureMainActivity")
    }
}

/**
 * 跳转电池性能模式
 * @param context Context
 */
fun jumpHighPerformance(context: Context) {
    if (context.checkPackName("com.oplus.battery")) {
        ShellUtils.fastCmd("am start com.oplus.battery/com.oplus.powermanager.fuelgaue.IntellPowerSaveScence")
    }
}

/**
 * 跳转到电池
 * @param context Context
 */
fun jumpBattery(context: Context) {
    if (context.checkPackName("com.oplus.battery")) {
        ShellUtils.fastCmd("am start com.oplus.battery/com.oplus.powermanager.fuelgaue.PowerConsumptionActivity")
    }
}

/**
 * 跳转进程管理
 * @param context Context
 */
fun jumpRunningApp(context: Context) {
    val packInfo = PackageUtils(context.packageManager).getPackageInfo(
        "com.android.settings", PackageManager.GET_ACTIVITIES
    ) ?: return
    val activity = packInfo.activities.find {
        it.name.contains("RunningApplicationActivity")
    }?.name
    if (activity == null) LogUtils.e(
        "jumpRunningApp", "activity", "RunningApplicationActivity is null", true
    )
    else ShellUtils.fastCmd("am start -n com.android.settings/$activity")
}

/**
 * 禁用组件
 * @receiver Context
 * @param value Boolean
 */
fun Context.setComponentDisabled(component: ComponentName, value: Boolean) {
    packageManager.setComponentEnabledSetting(
        component,
        if (value) COMPONENT_ENABLED_STATE_DISABLED else COMPONENT_ENABLED_STATE_ENABLED,
        DONT_KILL_APP
    )
}

/**
 * 获取组件状态
 * @receiver Context
 * @param component ComponentName
 * @return Int?
 */
fun Context.getComponentEnabled(component: ComponentName): Int? {
    return when (packageManager.getComponentEnabledSetting(component)) {
        COMPONENT_ENABLED_STATE_DEFAULT -> COMPONENT_ENABLED_STATE_DEFAULT
        COMPONENT_ENABLED_STATE_ENABLED -> COMPONENT_ENABLED_STATE_ENABLED
        COMPONENT_ENABLED_STATE_DISABLED -> COMPONENT_ENABLED_STATE_DISABLED
        COMPONENT_ENABLED_STATE_DISABLED_USER -> COMPONENT_ENABLED_STATE_DISABLED_USER
        COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED -> COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
        else -> null
    }
}

/**
 * 获取闪存信息
 * @return String
 */
val getFlashInfo
    get(): String = ShellUtils.fastCmd("cat /sys/class/block/sda/device/inquiry")
        .let { if ((it.isNotBlank())) formatSpace(it.replaceSpace.uppercase()) else "null" }

/**
 * 获取LCD信息
 */
val getLcdInfo: String
    get() : String = ShellUtils.fastCmd("cat /proc/devinfo/lcd | sed 's/^.*\t//g; s/$/\n/g; s/\n/ /g;'")
        .let { if ((it.isNotBlank())) it.replaceSpace.uppercase() else "null" }

/**
 * 获取PCB信息
 */
val getPcbInfo: String
    get() : String = ShellUtils.fastCmd("echo \$(getprop gsm.serial)\$(getprop vendor.gsm.serial)")
        .let { if ((it.isNotBlank())) it.replaceSpace.uppercase() else "null" }

/**
 * 获取SN信息
 */
val getSnInfo: String
    get() : String = ShellUtils.fastCmd("getprop ro.serialno").let {
        if ((it.isNotBlank())) it.replaceSpace.uppercase() else "null"
    }

/**
 * 正常编码中一般只会用到 [dp]/[sp] ;
 * 其中[dp]/[sp] 会根据系统分辨率将输入的dp/sp值转换为对应的px
 */
val Float.dp: Float // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Float.sp: Float // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics
    )

val Int.sp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

/**
 * 复制到剪贴板
 * @receiver Context
 * @param string CharSequence
 */
fun Context.copyStr(string: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(null, string)
    clipboard.setPrimaryClip(clipData)
}

/**
 * Base64转Bitmap图片
 * @param code String
 * @return Bitmap?
 */
fun base64ToBitmap(code: String): Bitmap? {
    val decode: ByteArray = Base64.decode(code.split(",")[1], Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decode, 0, decode.size)
}

/**
 * 返回MaterialDialog Title居中样式
 */
val dialogCentered get() = com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered

/**
 * 设置Preference图标显示
 * @receiver Context 上下文
 * @param resource Any? 传入对象
 * @param result Function2<Drawable?, Boolean, Unit> 输出
 */
fun Preference.setPrefsIconRes(resource: Any?, result: (Drawable?, Boolean) -> Unit) {
    if (context.getBoolean(SettingsPrefs, "hide_function_page_icon", false)) {
        result(null, false)
        return
    }
    val image: Drawable? = when (resource) {
        is Int -> ResourcesCompat.getDrawable(context.resources, resource, null)
        is Drawable -> resource
        is String -> context.getAppIcon(resource)
        else -> null
    }
    if (image == null || image.intrinsicWidth <= 0 || image.intrinsicHeight <= 0) {
        val icon =
            ResourcesCompat.getDrawable(context.resources, android.R.mipmap.sym_def_app_icon, null)
        result(icon, true)
        return
    }
    val bitmap = image.toBitmapOrNull()
    if (bitmap == null) {
        result(null, false)
        return
    }
    val drawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
    drawable.setAntiAlias(true)
    drawable.cornerRadius = 30F
    result(drawable, true)
}

/**
 * 修复Icon显示大小
 * @receiver Preference
 * @param icon Drawable?
 * @return Drawable?
 */
fun Preference.fixIconSize(icon: Drawable?): Drawable? {
    return if (icon != null && ((icon.intrinsicWidth < 48.dp) || (icon.intrinsicHeight < 48.dp))) {
        context.zoomDrawable(icon, 48.dp, 48.dp)
    } else icon
}

/**
 * 格式化Summary添加逗号
 * @param string Array<out String?>
 * @return String
 */
fun arraySummaryDot(vararg string: String?): String {
    return formatStringAuto(string.toList(), ",", false)
}

/**
 * 格式化Summary添加换行
 * @param string Array<out String?>
 * @return String
 */
fun arraySummaryLine(vararg string: String?): String {
    return formatStringAuto(string.toList(), "\n", false)
}

/**
 * 获取指定长度随机字符串
 * @param length Int 长度
 * @return String
 */
fun getRandomString(length: Int): String {
    val random = Random
    val sb = StringBuffer()
    for (i in 0 until length) {
        val number: Int = random.nextInt(3)
        var result: Long
        when (number) {
            0 -> {
                result = (Math.random() * 25 + 65).roundToLong()
                sb.append(Char(result.toUShort()).toString())
            }

            1 -> {
                result = (Math.random() * 25 + 97).roundToLong()
                sb.append(Char(result.toUShort()).toString())
            }

            2 -> sb.append(java.lang.String.valueOf(Random.nextInt(10)))
        }
    }
    return sb.toString()
}

/**
 * Hex To Byte
 */
fun hexToByte(inHex: String): Byte {
    return inHex.toInt(16).toByte()
}

/**
 * Base64加密
 * @param string String
 * @return String
 */
fun base64Encode(string: String): String {
    return Base64.encodeToString(string.toByteArray(), Base64.DEFAULT)
}

/**
 * Base64解密
 * @param string String
 * @return String
 */
fun base64Decode(string: String): String {
    return String(Base64.decode(string, Base64.DEFAULT))
}

/**
 * 判断语言首选项是否为中文
 * @param context Context
 * @return Boolean
 */
fun isZh(context: Context): Boolean {
    val locale = context.resources.configuration.locales
    val language = locale[0].language
    return language.endsWith("zh")
}

/**
 * 跳转到APP
 * @receiver Context
 * @param packNames Array<String>
 */
fun Context.openApp(packNames: Array<String>) {
    openApp(packNames.firstOrNull())
}

/**
 * 跳转到APP
 * @receiver Context
 * @param packName String
 */
fun Context.openApp(packName: String?) {
    if (packName.isNullOrBlank()) return
    packageManager.getLaunchIntentForPackage(packName)?.apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(this)
    }
}

/**
 * 跳转到应用详情界面
 * @param packName String
 * @param userId Int?
 */
fun Context.openAppDetailIntent(packName: String, userId: Int?) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    userId?.let { intent.putExtra("userId", it) }
    startActivity(intent)
}

/**
 * 跳转商店页面
 * @receiver Context
 * @param packName String
 */
fun Context.openMarketIntent(packName: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packName"))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    startActivity(intent)
}

/**
 * 重启作用域对话框
 * @receiver Context
 */
fun Context.restartMain() {
    val list = arrayOf(
        getString(R.string.restart_scope),
        getString(R.string.reboot),
        getString(R.string.fast_reboot)
    )
    MaterialAlertDialogBuilder(this, dialogCentered).apply {
        setCancelable(true)
        setItems(list) { _: DialogInterface?, i: Int ->
            when (i) {
                0 -> restartAllScope()
                1 -> ShellUtils.fastCmd("reboot")
                2 -> ShellUtils.fastCmd("killall zygote")
            }
        }
        show()
    }
    scope { withDefault { startCheckListFinal() } }
}

/**
 * 重启部分作用域对话框
 * @receiver Context
 * @param scopes Array<String>
 */
fun Context.restartScopes(scopes: Array<String>) {
    val list = arrayOf(
        getString(R.string.restart_scope), getString(R.string.restart_only_this_page_scope)
    )
    MaterialAlertDialogBuilder(this, dialogCentered).apply {
        setItems(list) { _, which ->
            when (which) {
                0 -> restartAllScope()
                1 -> restartAllScope(scopes)
            }
        }
        show()
    }
    scope { withDefault { startCheckListFinal() } }
}

/**
 * 获取Apk绝对路径
 * @param packName String 包名
 * @return ArrayMap<String, String>
 */
fun getPackageAbsolutePath(
    packName: String, ignoreCase: Boolean = false
): ArrayMap<String, String> {
    val map = ArrayMap<String, String>()
    val list = ArrayList<String>()
    Shell.cmd(
        "pm list packages -f | grep $packName" + if (ignoreCase) " -i" else ""
    ).to(list).exec()
    list.forEachIndexed { _, str ->
        val pack = str.replace("package:", "")
        val key = pack.substringAfterLast("=")
        val value = pack.substringBeforeLast("=")
        map[key] = value
    }
    return map
}

/**
 * 根据包名卸载APP
 * @param packName String 包名
 */
fun uninstallApp(packName: String, userId: String? = "0") {
    ShellUtils.fastCmd("pm uninstall --user $userId $packName")
}

/**
 * 根据包名卸载APP
 * @param packName String
 * @param userId String?
 * @return Boolean
 */
fun uninstallAppResult(packName: String, userId: String? = "0"): Boolean {
    return ShellUtils.fastCmdResult("pm uninstall --user $userId $packName")
}

/**
 * 强制删除APP
 * @param packName String 包名
 */
fun forceUninstallApp(packName: String) {
    getPackageAbsolutePath(packName).forEach { (k, v) ->
        if (k == packName) ShellUtils.fastCmd("rm -rf $v")
    }
}

/**
 * 卸载模块
 */
suspend fun Context.removeModule() {
    getUsers().forEach { uninstallApp(BuildConfig.APPLICATION_ID, it) }
    getUsers().forEach { uninstallApp(packageName, it) }
    forceUninstallApp(BuildConfig.APPLICATION_ID)
    forceUninstallApp(packageName)
}

/**
 * 退出模块
 * @receiver Context
 */
fun Context.exitModule() {
    (this as MainActivity).finishAndRemoveTask()
    exitProcess(0)
}

/**
 * 重启全部作用域
 * @receiver Context
 */
fun Context.restartAllScope() {
    val xposedScope = resources.getStringArray(R.array.xposed_scope)
    val commands = ArrayList<String>()
    for (scope in xposedScope) {
        if (scope == "android") continue
        if (scope.contains("systemui")) {
            commands.add("kill -9 `pgrep systemui`")
            continue
        }
        commands.add("pkill -9 $scope")
        commands.add("killall $scope")
        commands.add("am force-stop $scope")
        getAppVersion(scope)
    }
    MaterialAlertDialogBuilder(this).apply {
        setMessage(getString(R.string.restart_scope_message))
        setPositiveButton(getString(android.R.string.ok)) { _: DialogInterface?, _: Int ->
            scope(Dispatchers.Default) { ShellUtils.fastCmd(*commands.toTypedArray()) }
        }
        setNeutralButton(getString(android.R.string.cancel), null)
        show()
    }
}

/**
 * 重启部分作用域
 * @receiver Context
 * @param scopes Array<String>
 */
fun Context.restartAllScope(scopes: Array<String>) {
    val commands = ArrayList<String>()
    for (scope in scopes) {
        if (scope == "android") continue
        if (scope.contains("systemui")) {
            commands.add("kill -9 `pgrep systemui`")
            continue
        }
        commands.add("pkill -9 $scope")
        commands.add("killall $scope")
        commands.add("am force-stop $scope")
        getAppVersion(scope)
    }
    scope(Dispatchers.Default) { ShellUtils.fastCmd(*commands.toTypedArray()) }
}

/**
 * 绑定RootService反射服务
 * @receiver Context
 * @param clazz Class<*>
 * @param onConnected Function2<ComponentName?, IBinder?, Unit>
 * @param onDisconnected Function1<ComponentName?, Unit>
 */
fun Context.bindRootService(
    clazz: Class<*>,
    onConnected: (ComponentName?, IBinder?) -> Unit,
    onDisconnected: (ComponentName?) -> Unit = {}
) {
    val intent = Intent(this, clazz)
    RootService.bind(intent, object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) =
            onConnected(name, service)

        override fun onServiceDisconnected(name: ComponentName?) = onDisconnected(name)
    })
}

/**
 * 获取刷新率显示状态
 * @return Boolean
 */
fun getRefreshRateStatus(): Boolean = safeOfFalse {
//    Result: Parcel(NULL)
//    Result: Parcel(00000000    '....')
    val result = ShellUtils.fastCmd("service call SurfaceFlinger 1034 i32 2")
    if (result.isBlank()) return@safeOfFalse false
    return if (result.contains("Operation not permitted", true)) false
    else when (result.filterNumber.toIntOrNull()) {
        0 -> false
        1 -> true
        else -> false
    }
}

/**
 * 设置刷新率显示状态
 * @param status Boolean
 */
fun showRefreshRate(status: Boolean) {
    ShellUtils.fastCmd("service call SurfaceFlinger 1034 i32 ${if (status) 1 else 0}")
}

/**
 * 跳转fragment设置标题
 * @receiver Fragment
 * @param action Int Action ID
 * @param title String 页面标题
 */
fun Fragment.navigatePage(action: Int, title: CharSequence? = "Title") = try {
    findNavController().navigate(action, Bundle().apply {
        putCharSequence("title_label", title)
    })
} catch (_: IllegalArgumentException) {

}


/**
 * 跳转fragment传递参数
 * @receiver Fragment
 * @param action Int
 * @param bundle Bundle?
 */
fun Fragment.navigatePage(action: Int, bundle: Bundle?) = try {
    findNavController().navigate(action, bundle)
} catch (_: IllegalArgumentException) {

}


/**
 * 获取屏幕状态
 * (true -> 竖屏 ORIENTATION_PORTRAIT)
 * (false -> 横屏 ORIENTATION_LANDSCAPE)
 * @param view View/Context/Resources
 * @param result Function1<Boolean, Unit>
 */

fun getScreenOrientation(view: View, result: (Boolean) -> Unit) {
    getScreenOrientation(view.resources) { result(it) }
}

/**
 * 获取屏幕状态
 * (true -> 竖屏 ORIENTATION_PORTRAIT)
 * (false -> 横屏 ORIENTATION_LANDSCAPE)
 * @param context View/Context/Resources
 * @param result Function1<Boolean, Unit>
 */
fun getScreenOrientation(context: Context, result: (Boolean) -> Unit) {
    getScreenOrientation(context.resources) { result(it) }
}

/**
 * 获取屏幕状态
 * (true -> 竖屏 ORIENTATION_PORTRAIT)
 * (false -> 横屏 ORIENTATION_LANDSCAPE)
 * @param resource View/Context/Resources
 * @param result Function1<Boolean, Unit>
 */
fun getScreenOrientation(resource: Resources, result: (Boolean) -> Unit) {
    val mConfiguration: Configuration = resource.configuration
    if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        result(true)
    }
    if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        result(false)
    }
}

/**
 * 获取设备用户
 * @return Array<String>
 */
suspend fun getUsers(): Array<String> {
    return withDefault {
        ShellUtils.fastCmd("ls /data/user/ -mF").let {
            if (it.isNotBlank()) {
                it.replaceSpace.replace("/", "").split(",").toTypedArray()
            } else arrayOf()
        }
    }
}

suspend fun Context.getQSlist(): ArrayList<String> {
    return withDefault {
        val cachelist = ArrayList<String>()
        getUsers().forEach { u ->
            val dir1 = getString(R.string.tencent_files, u)
            val command1 = "if [[ -d $dir1 ]]; then\n  ls $dir1 -mF\nfi"
            val list1 = ShellUtils.fastCmd(command1).let { its ->
                if (its.isNotBlank()) {
                    its.replaceSpace.split(",").toMutableList().apply {
                        removeIf { it.contains("/").not() }
                        removeIf { Pattern.matches(".*[a-zA-Z]+.*", it) }
                    }
                } else arrayListOf()
            }
            val dir2 = getString(R.string.tencent_qstore, u)
            val command2 = "if [[ -d $dir2 ]]; then\n  ls $dir2 -mF\nfi"
            var list2 = ShellUtils.fastCmd(command2).let { its ->
                if (its.isNotBlank()) {
                    its.replaceSpace.split(",").toMutableList().apply {
                        removeIf { it.contains("/").not() }
                        removeIf { Pattern.matches(".*[a-zA-Z]+.*", it) }
                    }
                } else arrayListOf()
            }
            if (list2.isEmpty()) list2 = list1
            val finals = list1.filter { list2.contains(it) }.toMutableList().apply {
                forEachIndexed { index, s -> this[index] = s.replace("/", "") }
            }
            cachelist.addAll(finals)
        }
        cachelist
    }
}

suspend fun Context.getCSid(): ArrayList<String> {
    return withDefault {
        val cachelist = ArrayList<String>()
        getUsers().forEach { u ->
            val dir = getString(R.string.cool_black, u)
            val command = "cat $dir | grep 'name=\"uid\"'"
            val str = ShellUtils.fastCmd(command).let { its ->
                if (its.isNotBlank()) {
                    its.replaceSpace.filterNumber
                } else ""
            }
            if (str.isNotBlank()) cachelist.add(str)
        }
        cachelist.apply { removeIf { it.isBlank() } }
    }
}

/**
 * 获取文本颜色(ColorSpan)
 * @param char CharSequence
 * @return Int? 返回Span数组中下标为0的前景色
 */
fun getCharColor(char: CharSequence): Int? {
    val sp = SpannableString(char)
    val colorSpan = sp.getSpans(0, sp.length, ForegroundColorSpan::class.java)
    return if (colorSpan != null && colorSpan.isNotEmpty()) colorSpan[0].foregroundColor else null
}

/**
 * 获取文本Span数组
 * @param char CharSequence
 * @return Array<out ForegroundColorSpan>?
 */
fun getCharSpans(char: CharSequence): Array<out ForegroundColorSpan>? {
    val colorSpans = SpannableString(char).getSpans(0, char.length, ForegroundColorSpan::class.java)
    return if (colorSpans == null || colorSpans.isEmpty()) null else colorSpans
}

/**
 * 缩放Drawable
 * @receiver Context
 * @param drawable Drawable
 * @param width Int
 * @param height Int
 * @return Drawable
 */
fun Context.zoomDrawable(drawable: Drawable, width: Int, height: Int): Drawable {
    val oldBmp = drawable.toBitmap()
    val newBmp = Bitmap.createScaledBitmap(oldBmp, width, height, true)
    return BitmapDrawable(resources, newBmp)
}

fun Context.checkVerify() = safeOf({ exitModule() }) {
    val packInfo =
        PackageUtils(packageManager).getPackageInfo(BuildConfig.APPLICATION_ID, 0) ?: return@safeOf
    if (packInfo.packageName != packageName || packInfo.versionName != getVersionName || packInfo.longVersionCode != getVersionCode.toLong()) {
        exitModule()
    }
}

/**
 * 判断是否MTK机型
 */
val isMTK get() = Pattern.compile("mt[0-9]*").matcher(Build.HARDWARE).find()

/**
 * 获取系统当前小时格式
 * @receiver Context
 * @return Boolean
 */
val Context.is24
    get() = Settings.System.getString(
        contentResolver, Settings.System.TIME_12_24
    ) == "24"

/**
 * 调用锁屏事件 (反射)
 * @param context Context
 */
fun closeScreen(context: Context) {
    val service = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    service.current().method { name = "goToSleep";param(LongType) }.call(SystemClock.uptimeMillis())
}

/**
 * Fragment快捷设置MenuProvider
 * @receiver Fragment
 * @param menuId Int Menu Resource ID
 * @param onMenuSelected Function1<MenuItem, Boolean>
 */
fun Fragment.setupMenuProvider(@MenuRes menuId: Int, onMenuSelected: (MenuItem) -> Boolean) =
    (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
            menuInflater.inflate(menuId, menu)

        override fun onMenuItemSelected(menuItem: MenuItem) = onMenuSelected(menuItem)
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)

/**
 * Fragment快捷设置MenuProvider
 * @receiver Fragment
 * @param menuProvider MenuProvider
 */
fun Fragment.setupMenuProvider(menuProvider: MenuProvider) =
    (requireActivity() as MenuHost).addMenuProvider(
        menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED
    )

/**
 * 检查宿主模块版本是否匹配
 * @receiver Context
 * @param isValied Function1<Boolean, Unit>
 */
fun Context.checkModuleValied(isValied: (Boolean) -> Unit) {
    dataChannel("com.android.systemui").checkingVersionEquals(result = isValied)
}

val redOneTextColor = Color.parseColor("#c41442")

/**
 * 计算本地电池健康度
 * @receiver Context
 * @param isDebug Boolean
 * @return Int
 */
fun Context.calcLocalHealth(isDebug: Boolean = false): Int {
    try {
        val sohFile = if (SDK >= A13) File("/sys/class/oplus_chg/battery/battery_soh")
        else File("/sys/class/power_supply/battery/batt_soh")
        val sohValue = safeOfNull {
            BufferedReader(FileReader(sohFile)).readLine().replaceSpace.toIntOrNull()
        } ?: -1
        if (isDebug) LogUtils.d("calcLocalHealth", "sohValue", "$sohValue", true)
        if (sohValue in 1..100) return sohValue

        val fccFile = if (SDK >= A13) File("/sys/class/oplus_chg/battery/battery_fcc")
        else File("/sys/class/power_supply/battery/batt_fcc")
        if (isDebug) LogUtils.d("calcLocalHealth", "curFile", fccFile.path, true)
        val curValue = safeOfNull {
            BufferedReader(FileReader(fccFile)).readLine().replaceSpace.toIntOrNull()
        } ?: -1
        if (isDebug) LogUtils.d("calcLocalHealth", "curValue", "$curValue", true)
        if (curValue <= 0) return -1

        val designValue = PowerProfile(this).batteryCapacity
        if (isDebug) LogUtils.d("calcLocalHealth", "designValue", "$designValue", true)
        val calc = safeOfNull { (curValue / designValue * 100.0).roundToInt() } ?: -1
        if (isDebug) LogUtils.d("calcLocalHealth", "calc", "$calc", true)
        return if (calc > 100) calc / 1000 else calc
    } catch (e: Exception) {
        YLog.error("Calc Local Health Error", e)
        return -1
    }
}

fun logcatToFile(file: File): Boolean {
    return try {
        if (!file.exists()) file.createNewFile()
        ShellUtils.fastCmdResult("logcat -d -f ${file.absolutePath}")
    } catch (e: Exception) {
        LogUtils.e("logcatToFile", "logcat", "$e", true)
        false
    }
}

/**
 * 发送Prefs键值到dataChannel
 * @receiver Context
 * @param packName String
 * @param key String
 * @param newValue Any
 */
fun Context.sendPrefsValue(packName: String, key: String, newValue: Any) {
    dataChannel(packName).put(key, newValue)
}

/**
 * 逆转字符串数组
 * @param str
 * @return
 */
fun stringReverse(str: String): String {
    return str.toList().reversed().toString().replaceSpace.replace(",", "").replace("[", "")
        .replace("]", "")
}

/**
 * 创建并显示BottomSheetDialog
 * @receiver Context
 * @param rootView View?
 * @return BottomSheetDialog
 */
fun Context.showBottomSheet(rootView: View? = null): BottomSheetDialog {
    return BottomSheetDialog(this).apply {
        if (rootView != null) setContentView(rootView)
        show()
    }
}

/**
 * 打开链接
 * @receiver Context
 * @param url String
 */
fun Context.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}