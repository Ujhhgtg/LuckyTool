package com.luckyzyx.luckytool.ui.fragment.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.ArraySet
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.utils.Base64CodeUtils
import com.luckyzyx.luckytool.utils.DonateUtils
import com.luckyzyx.luckytool.utils.FileUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.OtherPrefs
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.backupAllPrefs
import com.luckyzyx.luckytool.utils.base64Decode
import com.luckyzyx.luckytool.utils.base64Encode
import com.luckyzyx.luckytool.utils.clearAllPrefs
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.logcatToFile
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.openUrl
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putInt
import com.luckyzyx.luckytool.utils.putString
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.setComponentDisabled
import com.luckyzyx.luckytool.utils.showToast
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.system.exitProcess

@Obfuscate
class SettingsFragment : ModulePreferenceFragment() {
    private val backupData = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) {
        if (it != null) {
            writeBackupData(requireActivity(), it)
        }
    }
    private val restoreData = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            writeRestoreData(requireActivity(), FileUtils.readFromUri(requireActivity(), it))
        }
    }

    private fun writeBackupData(context: Context, uri: Uri) {
        val json = JSONObject()
        val dataMapList = context.backupAllPrefs(ModulePrefs, SettingsPrefs, OtherPrefs)
        dataMapList?.keys?.forEach { prefs ->
            val jsons = JSONObject()
            val data = dataMapList[prefs]
            data?.keys?.forEach { key ->
                data[key].apply {
                    if (this?.javaClass?.simpleName == "HashSet") {
                        val arr = JSONArray()
                        val value = (this as HashSet<*>).toTypedArray()
                        for (i in value.indices) {
                            arr.put(value[i])
                        }
                        jsons.put(key, arr)
                    } else {
                        jsons.put(key, this)
                    }
                }
            }
            json.put(prefs, jsons)
        }
        val str = base64Encode(json.toString())
        try {
            context.contentResolver.openFileDescriptor(uri, "w")?.use { its ->
                FileOutputStream(its.fileDescriptor).use {
                    it.write(str.toByteArray())
                }
            }
            context.showToast(getString(R.string.data_backup_complete))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            context.showToast(getString(R.string.data_backup_error))
        } catch (e: IOException) {
            e.printStackTrace()
            context.showToast(getString(R.string.data_backup_error))
        }
    }

    private fun writeRestoreData(context: Context, data: String) {
        val json = JSONObject(base64Decode(data))
        if (json.length() <= 0) return
        json.keys().forEach { prefs ->
            val prefsDatas = json.getJSONObject(prefs)
            if (prefsDatas.length() > 0) {
                prefsDatas.keys().forEach { key ->
                    val value = prefsDatas.get(key)
                    when (value.javaClass.simpleName) {
                        "Boolean" -> context.putBoolean(prefs, key, value as Boolean)
                        "Integer" -> context.putInt(prefs, key, value as Int)
                        "JSONArray" -> {
                            val set = ArraySet<String>()
                            val list = value as JSONArray
                            for (i in 0 until list.length()) {
                                set.add(list[i] as String)
                            }
                            context.putStringSet(prefs, key, set)
                        }

                        "String" -> context.putString(prefs, key, value as String)
                        else -> context.showToast("Error: $key")
                    }
                }
            }
        }
        context.showToast(getString(R.string.data_restore_complete))
        (activity as MainActivity).restart()
    }

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = SettingsPrefs
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            //主题
            addPreference(PreferenceCategory(context).apply {
                setTitle(R.string.theme_title)
                setSummary(R.string.theme_title_summary)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                key = "use_dynamic_color"
                setDefaultValue(false)
                setTitle(R.string.use_dynamic_color)
                setSummary(R.string.use_dynamic_color_summary)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            addPreference(DropDownPreference(context).apply {
                key = "dark_theme"
                title = getString(R.string.dark_theme)
                summary = "%s"
                setEntries(R.array.dark_theme)
                entryValues = resources.getStringArray(R.array.dark_theme_value)
                setDefaultValue("MODE_NIGHT_FOLLOW_SYSTEM")
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            //其他
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.other_settings)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                key = "auto_check_update"
                title = getString(R.string.auto_check_update)
                summary = getString(R.string.auto_check_update_summary)
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                key = "tile_auto_start"
                title = getString(R.string.tile_auto_start)
                summary = getString(R.string.tile_auto_start_summary)
                setDefaultValue(true)
                isIconSpaceReserved = false
            })
            addPreference(SwitchPreference(context).apply {
                key = "hide_function_page_icon"
                title = getString(R.string.hide_function_page_icon)
                setDefaultValue(false)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, _ ->
                    (activity as MainActivity).restart()
                    true
                }
            })
            addPreference(SwitchPreference(context).apply {
                key = "hide_desktop_module_icon"
                setDefaultValue(false)
                title = getString(R.string.hide_desktop_module_icon)
                summary = getString(R.string.hide_desktop_module_icon_summary)
                isIconSpaceReserved = false
                setOnPreferenceChangeListener { _, newValue ->
                    context.setComponentDisabled(
                        ComponentName(
                            context.packageName, "${context.packageName}.Hide"
                        ), newValue as Boolean
                    )
                    true
                }
            })
            //备份
            addPreference(PreferenceCategory(context).apply {
                title = getString(R.string.backup_restore_clear)
                key = "backup_restore_clear"
                isIconSpaceReserved = false
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.get_log_cat_log)
                key = "get_log_cat_log"
                isPersistent = false
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    val name = "logcat_" + formatDate("yyMMdd_HHmmss") + ".log"
                    val file = FileUtils.checkLogCatDir(context, name)
                    if (logcatToFile(file)) FileUtils.shareFile(context, "Share LogCat File", file)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.backup_data)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    FileUtils.checkDownloadDir(context, "LuckyTool").apply {
                        if (isFile) delete()
                        if (!exists()) mkdirs()
                    }
                    val fileName = "LuckyTool_" + formatDate("yyyyMMdd_HHmmss") + "_backup.json"
                    backupData.launch(fileName)
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.restore_data)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    FileUtils.checkDownloadDir(context, "LuckyTool").apply {
                        if (isFile) delete()
                        if (!exists()) mkdirs()
                    }
                    restoreData.launch("application/json")
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.clear_all_data)
                summary = getString(R.string.clear_all_data_summary)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    MaterialAlertDialogBuilder(context).apply {
                        setMessage(getString(R.string.clear_all_data_message))
                        setPositiveButton(android.R.string.ok) { _, _ ->
                            context.clearAllPrefs(ModulePrefs, SettingsPrefs, OtherPrefs)
                            exitProcess(0)
                        }
                        setNeutralButton(android.R.string.cancel, null)
                        show()
                    }
                    true
                }
            })
            //关于
            addPreference(PreferenceCategory(context).apply {
                setTitle(R.string.about_title)
                isIconSpaceReserved = false
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.donate)
                summary = getString(R.string.donate_summary)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    val donateList = arrayListOf<CharSequence>(
                        getString(R.string.qq),
                        getString(R.string.wechat),
                        getString(R.string.alipay),
                        getString(R.string.donation_list)
                    )
                    if (!isZh(context)) {
                        donateList.add(3, getString(R.string.patreon))
                        donateList.add(4, getString(R.string.paypal))
                    }
                    MaterialAlertDialogBuilder(context).apply {
                        setItems(donateList.toTypedArray()) { _, which ->
                            when (which) {
                                0 -> DonateUtils.showQRCode(context, Base64CodeUtils.qqCode)
                                1 -> DonateUtils.showQRCode(context, Base64CodeUtils.wechatCode)
                                2 -> DonateUtils.showQRCode(context, Base64CodeUtils.alipayCode)
                                3 -> if (isZh(context)) {
                                    navigatePage(
                                        R.id.action_nav_setting_to_donateFragment,
                                        getString(R.string.donation_list)
                                    )
                                } else startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.patreon.com/LuckyTool")
                                    )
                                )

                                4 -> startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW, Uri.parse("https://paypal.me/luckyzyx")
                                    )
                                )

                                5 -> navigatePage(
                                    R.id.action_nav_setting_to_donateFragment,
                                    getString(R.string.donation_list)
                                )
                            }
                        }
                    }.show()
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.feedback_download)
                summary = getString(R.string.feedback_download_summary)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    val updatelist = arrayOf(
                        getString(R.string.coolmarket),
                        getString(R.string.module_doc),
                        getString(R.string.qq_chat_group),
                        getString(R.string.qq_channel),
                        getString(R.string.telegram_channel),
                        getString(R.string.telegram_group),
                        getString(R.string.lsposed_repo)
                    )
                    MaterialAlertDialogBuilder(context).setItems(updatelist) { _, which ->
                        when (which) {
                            0 -> context.openUrl("coolmarket://u/1930284")
                            1 -> context.openUrl("https://luckyzyx.gitlab.io/LuckyTool_Doc")
                            2 -> context.openUrl("http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=3fYu6lT8IHrBPKAfFTNSHbd8wcWX0oGs&authKey=dyIpjTWH8KWHMU3v6gI05T0bAzr6XigJKasMiCwmco1%2F8BRtPCN%2B1zOGgXyK7IUB&noverify=0&group_code=663884734")
                            3 -> context.openUrl("https://pd.qq.com/s/ahjm4zyxb")
                            4 -> context.openUrl("https://t.me/LuckyTool")
                            5 -> context.openUrl("https://t.me/+F42pfv-c0h4zNDc9")
                            6 -> context.openUrl("https://modules.lsposed.org/module/com.luckyzyx.luckytool")
                        }
                    }.show()
                    true
                }
            })
            addPreference(Preference(context).apply {
                title = getString(R.string.participate_translation)
                summary = getString(R.string.participate_translation_summary)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse("https://crwd.in/luckytool")
                        )
                    )
                    true
                }
            })
            addPreference(Preference(context).apply {
                setTitle(R.string.open_source)
                setSummary(R.string.open_source_summary)
                isVisible = false
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    navigatePage(R.id.action_nav_setting_to_sourceFragment, title)
                    true
                }
            })
        }
    }
}