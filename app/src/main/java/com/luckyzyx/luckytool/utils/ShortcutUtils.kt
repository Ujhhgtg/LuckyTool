package com.luckyzyx.luckytool.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.ShortcutBean
import com.luckyzyx.luckytool.ui.activity.ShortcutActivity

@Obfuscate
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ShortcutUtils(val context: Context) {

    /**
     * 获取内置快捷方式支持列表
     * @receiver Context
     * @return ArrayList<ShortcutBean>
     */
    fun getDefaultShortcutList(): ArrayList<ShortcutBean> {
        val defaultList = ArrayList<ShortcutBean>()
        safeOfNull {
            ShortcutBean(
                "module_shortcut_status_lsposed", "LSPosed", Icon.createWithResource(
                    context.packageName,
                    android.R.mipmap.sym_def_app_icon
                )
            )
        }?.let { defaultList.add(it) }
        val existOplusGame =
            context.checkPackName("com.oplus.games") && context.checkResolveActivity(
                Intent().setClassName(
                    "com.oplus.games",
                    "business.compact.activity.GameBoxCoverActivity"
                )
            )
        if (existOplusGame) safeOfNull {
            ShortcutBean(
                "module_shortcut_status_oplusgames",
                context.getAppLabel("com.oplus.games").toString(),
                Icon.createWithResource(
                    context.packageName, R.mipmap.oplusgames_icon
                )
            )
        }?.let { defaultList.add(it) }
        safeOfNull {
            ShortcutBean(
                "module_shortcut_status_chargingtest", context.getString(R.string.charging_test),
                Icon.createWithResource(
                    context.packageName, R.drawable.ic_baseline_charging_station_24
                )
            )
        }?.let { defaultList.add(it) }
        safeOfNull {
            ShortcutBean(
                "module_shortcut_status_processmanager",
                context.getString(R.string.process_manager),
                Icon.createWithResource(
                    context.packageName, android.R.mipmap.sym_def_app_icon
                )
            )
        }?.let { defaultList.add(it) }
        safeOfNull {
            ShortcutBean(
                "module_shortcut_status_performance",
                context.getString(R.string.high_performance_mode),
                Icon.createWithResource(
                    context.packageName, R.drawable.baseline_device_thermostat_24
                )
            )
        }?.let { defaultList.add(it) }
        defaultList.forEachIndexed { _, bean ->
            val intent = when (bean.key) {
                "module_shortcut_status_oplusgames" -> Intent(Intent.ACTION_VIEW).apply {
                    putExtra("Shortcut", bean.key)
                    setClassName(
                        "com.oplus.games",
                        "business.compact.activity.GameBoxCoverActivity"
                    )
                }

                else -> Intent(Intent.ACTION_VIEW).apply {
                    setClass(context, ShortcutActivity::class.java)
                    putExtra("Shortcut", bean.key)
                }
            }
            val isEnable = context.getBoolean(SettingsPrefs, bean.key, false)
            bean.intent = intent
            bean.isEnable = isEnable
        }
        return defaultList
    }

    fun getEnabledShortcutList(): ArrayList<ShortcutInfo> {
        val list = ArrayList<ShortcutInfo>()
        getDefaultShortcutList().forEach {
            if (it.isEnable) list.add(createShortcutInfo(it.key, it.label, it.icon, it.intent))
        }
        return list
    }

    /**
     * 设置快捷方式启用状态
     * @param key String
     * @param status Boolean
     */
    fun setShortcutStatus(key: String, status: Boolean) {
        context.putBoolean(SettingsPrefs, key, status)
        if (!status) {
            removeDynamicShortcuts(arrayListOf(key))
        }
    }

    /**
     * 获取模块图标状态
     * @receiver Context
     * @return Boolean
     */
    fun getIconStatus(): Boolean {
        return when (context.getComponentEnabled(
            ComponentName(context.packageName, "${context.packageName}.Hide")
        )) {
            0 -> true
            1 -> true
            else -> false
        }
    }

    /**
     * 创建快捷方式信息
     * @receiver Context
     * @param id String
     * @param label String
     * @param icon Icon
     * @param intent Intent
     * @return ShortcutInfo
     */
    fun createShortcutInfo(id: String, label: String, icon: Icon?, intent: Intent?): ShortcutInfo {
        return ShortcutInfo.Builder(context, id).apply {
            setShortLabel(label)
            setIcon(icon)
            intent?.let { setIntent(it) }
        }.build()
    }

    /**
     * 获取动态快捷方式
     * @return List<ShortcutInfo>?
     */
    fun getDynamicShortcuts(): List<ShortcutInfo>? = safeOfNull {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
                as ShortcutManager
        shortcutManager.dynamicShortcuts
    }

    /**
     * 设置动态快捷方式
     * @return Any
     */
    fun updateDynamicShortcuts() = safeOf({
        context.showToast("Set Dynamic Shortcuts Error!")
    }) {
        val shortcutManager =
            context.getSystemService(ShortcutManager::class.java) as ShortcutManager
        shortcutManager.dynamicShortcuts = getEnabledShortcutList()
    }

    /**
     * 设置动态快捷方式
     * @receiver Context
     * @param list Array<out ShortcutInfo>
     */
    fun updateDynamicShortcuts(list: ArrayList<ShortcutInfo>) = safeOf({
        context.showToast("Set Dynamic Shortcuts Error!")
    }) {
        val shortcutManager =
            context.getSystemService(ShortcutManager::class.java) as ShortcutManager
        shortcutManager.dynamicShortcuts = list
    }

    /**
     * 根据ID移除快捷方式
     * @receiver Context
     * @param list Array<out String>
     */
    fun removeDynamicShortcuts(list: ArrayList<String>) {
        val shortcutManager =
            context.getSystemService(ShortcutManager::class.java) as ShortcutManager
        shortcutManager.removeDynamicShortcuts(list)
    }
}