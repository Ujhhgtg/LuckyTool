package com.luckyzyx.luckytool.ui.fragment.base

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.preference.Preference
import com.drake.net.utils.scopeLife
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.PrefsItem
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.getOSVersionName
import com.luckyzyx.luckytool.utils.restartScopes
import com.luckyzyx.luckytool.utils.safeOfNull
import com.luckyzyx.luckytool.utils.setupMenuProvider

@Obfuscate
@Suppress("unused")
abstract class BaseScopePreferenceFeagment : ModulePreferenceFragment(), MenuProvider {

    //ColorOS系统参数
    val osName = getOSVersionName
    val osCode = getOSVersionCode

    /**
     * 相关作用域
     */
    open val scopes = arrayOf<String>()

    /**
     * 是否启用重启菜单
     */
    open val isEnableRestartMenu: Boolean = false

    /**
     * 是否启用跳转菜单
     */
    open val isEnableOpenMenu: Boolean = false

    /**
     * 当前Prefs存储名称
     */
    abstract val currentPrefsName: String

    /**
     * 跳转Action
     */
    open val navigateFragmentId: Int = -1

    abstract fun Context.loadPreferences(): ArrayList<Preference>

    open fun readPrefsItem(context: Context): ArrayList<PrefsItem> {
        return ArrayList<PrefsItem>().apply {
            context.loadPreferences().forEachIndexed { _, preference ->
                val item = PrefsItem(
                    preference.key,
                    preference.title,
                    preference.summary,
                    preference.isVisible
                )
                add(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        if (currentPrefsName.isNotBlank()) preferenceManager.sharedPreferencesName =
            currentPrefsName
        val prefsScreen = preferenceManager.createPreferenceScreen(requireActivity())
        scopeLife {
            requireActivity().loadPreferences().forEachIndexed { index, preference ->
                try {
                    prefsScreen.addPreference(preference)
                } catch (t: Throwable) {
                    LogUtils.e(
                        "${this@BaseScopePreferenceFeagment.javaClass} loadPreferences",
                        "$index | ${preference.key} | ${preference.title}",
                        "$t",
                        true
                    )
                }
            }
            preferenceScreen = prefsScreen
        }
        arguments?.getCharSequence("title_text")?.let {
            safeOfNull { (activity as MainActivity).supportActionBar?.title = it }
        }
    }

    /**
     * 自定义跳转菜单点击事件
     * @return Unit
     */
    open fun callOpenMenu() {}

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        if (isEnableRestartMenu) menu.add(0, 1, 0, getString(R.string.menu_reboot)).apply {
            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        if (isEnableOpenMenu) menu.add(0, 2, 0, getString(R.string.common_words_open)).apply {
            setIcon(R.drawable.baseline_open_in_new_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            1 -> requireActivity().restartScopes(scopes)
            2 -> callOpenMenu()
        }
        return true
    }
}