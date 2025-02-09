package com.luckyzyx.luckytool.ui.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.highcapable.yukihookapi.YukiHookAPI
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.FragmentHomeBinding
import com.luckyzyx.luckytool.service.GlobalFuncService
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.utils.CommandUtils
import com.luckyzyx.luckytool.utils.DeviceUtils
import com.luckyzyx.luckytool.utils.DonateUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.RestartMenuUtils
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.UpdateUtils
import com.luckyzyx.luckytool.utils.copyStr
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getDeviceInfo
import com.luckyzyx.luckytool.utils.getVersionCode
import com.luckyzyx.luckytool.utils.getVersionName
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.setupMenuProvider
import com.luckyzyx.luckytool.utils.showToast
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils

@Obfuscate
class HomeFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentHomeBinding

    private var enableModule: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        enableModule = requireActivity().getBoolean(ModulePrefs, "enable_module", false)
        refreshModuleStatus()

        binding.enableModule.apply {
            text = context.getString(R.string.enable_module)
            isChecked = enableModule
            setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    context.putBoolean(ModulePrefs, "enable_module", isChecked)
                    (activity as MainActivity).restart()
                }
            }
        }

//        if (requireActivity().getBoolean(SettingsPrefs, "auto_check_update", true)) {
        val isDev = requireActivity().getBoolean(SettingsPrefs, "hidden_function")
        UpdateUtils(requireActivity(), isDev).checkUpdate(
            getVersionName, getVersionCode
        ) { versionName, versionCode, function ->
            if (getVersionCode < versionCode) {
                function()
                binding.updateView.isVisible = true
                binding.updateInfo.apply {
                    text =
                        getString(R.string.check_update_hint) + "  -->  $versionName($versionCode)"
                }
                binding.statusCard.setOnClickListener { function() }
            }
            binding.statusCard.apply {
                if (isDev) setOnLongClickListener {
                    function()
                    true
                }
            }
        }

        binding.systemInfo.apply {
            setOnLongClickListener {
                context.copyStr(DeviceUtils.getOTACOnfigs())
                context.showToast("Copy Device OTA Data Success!")
                true
            }
        }

        binding.donateTvTitle.text = getString(R.string.donate_tv_title) + " by: 忆清鸣、luckyzyx"
        binding.donateTvView.apply {
            setOnClickListener {
                val url = if (isZh(context)) "https://docs.qq.com/doc/DS2ZDZlNIeUlpdlV1"
                else "https://luckyzyx.github.io/LuckyTool_Doc/en/donate"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            setOnLongClickListener {
                val donateList = arrayListOf(
                    getString(R.string.qq),
                    getString(R.string.wechat),
                    getString(R.string.alipay),
                )
                if (!isZh(context)) {
                    donateList.add(3, getString(R.string.patreon))
//                    donateList.add(4, getString(R.string.paypal))
                }
                MaterialAlertDialogBuilder(context).apply {
                    setItems(donateList.toTypedArray()) { _, which ->
                        when (which) {
                            0 -> DonateUtils.showQRCode(context, which)
                            1 -> DonateUtils.showQRCode(context, which)
                            2 -> DonateUtils.showQRCode(context, which)
                            3 -> if (!isZh(context)) startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.patreon.com/LuckyTool")
                                )
                            )
                        }
                    }
                }.show()
                true
            }
        }

        binding.authorized.apply {
            if (isZh(context)) {
                isVisible = true
                textSize = 16F
                text = context.getString(R.string.authorized)
                setTextColor(Color.RED)
            }
            setOnClickListener {
                val url = "https://luckyzyx.github.io/LuckyTool_Doc/use/download_link"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }

        binding.tv.apply {
            isVisible = false
        }
    }

    private fun initSystemInfoView() {
        GlobalFuncService.get(activity) {
            val deviceInfo = activity?.getDeviceInfo(it)
            if (deviceInfo.isNullOrBlank()) return@get
            binding.systemInfoLoading.isVisible = false
            binding.systemInfo.apply {
                gravity = Gravity.START
                text = deviceInfo
                isVisible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initSystemInfoView()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.menu_reboot)).apply {
            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 2, 0, getString(R.string.menu_settings)).apply {
            setIcon(R.drawable.ic_baseline_info_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == 1) RestartMenuUtils.showMainRestartMenu(requireActivity())
        if (menuItem.itemId == 2) {
            MaterialAlertDialogBuilder(requireActivity()).apply {
                setTitle(getString(R.string.about_author))
                setView(MaterialTextView(context).apply {
                    var hideFunc = context.getBoolean(SettingsPrefs, "hidden_function", false)
                    setPadding(20.dp)
                    text = if (hideFunc) "忆清鸣、luckyzyx T" else "忆清鸣、luckyzyx"
                    setOnLongClickListener {
                        context.putBoolean(SettingsPrefs, "hidden_function", !hideFunc)
                        hideFunc = context.getBoolean(SettingsPrefs, "hidden_function", false)
                        text = if (hideFunc) "忆清鸣、luckyzyx T" else "忆清鸣、luckyzyx"
                        true
                    }
                })
                show()
            }
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    fun refreshModuleStatus() {
        when {
            YukiHookAPI.Status.isXposedModuleActive && enableModule -> {
                binding.statusIcon.setImageResource(R.drawable.ic_round_check_24)
            }

            else -> {
                binding.statusCard.setCardBackgroundColor(Color.GRAY)
                binding.statusIcon.setImageResource(R.drawable.ic_round_warning_24)
            }
        }
        binding.moduleStatus.text = when {
            YukiHookAPI.Status.isXposedModuleActive && enableModule.not() -> getString(R.string.module_is_disabled)
            YukiHookAPI.Status.isXposedModuleActive -> getString(R.string.module_isactivated)
            else -> getString(R.string.module_notactive)
        }

        binding.moduleVersion.apply {
            text = "${getString(R.string.module_version)} $getVersionName ($getVersionCode)" +
                    " ${BuildConfig.BUILD_TYPE.uppercase()}"
        }

        binding.rootVersion.apply {
            val rootSource = if (Shell.cmd("magisk").exec().isSuccess) {
                ShellUtils.fastCmd("magisk -v") + " (" + ShellUtils.fastCmd("magisk -V") + ")"
            } else if (Shell.cmd("su -h").exec().isSuccess) {
                ShellUtils.fastCmd("su -v") + " (" + ShellUtils.fastCmd("su -V") + ")"
            } else "Other or Error"
            text = "${getString(R.string.root_source)} $rootSource"
        }

        binding.frameworkVersion.apply {
            val moduleProp = Shell.cmd("cat ${CommandUtils.lspProp}").exec().out
            val name = moduleProp.find { it.startsWith("name=") }?.substringAfter("=")
            val version = moduleProp.find { it.startsWith("version=") }?.substringAfter("=")
            text = "${getString(R.string.framework_version)} $name $version"
        }
    }
}