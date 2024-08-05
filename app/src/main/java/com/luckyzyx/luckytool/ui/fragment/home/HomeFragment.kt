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
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.highcapable.yukihookapi.YukiHookAPI
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IGlobalFuncController
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogOplusotaLayoutBinding
import com.luckyzyx.luckytool.databinding.FragmentHomeBinding
import com.luckyzyx.luckytool.service.controller.GlobalFuncControllerService
import com.luckyzyx.luckytool.ui.activity.MainActivity
import com.luckyzyx.luckytool.utils.Base64CodeUtils
import com.luckyzyx.luckytool.utils.DeviceUtils
import com.luckyzyx.luckytool.utils.DonateUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.UpdateUtils
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.copyStr
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getDeviceInfo
import com.luckyzyx.luckytool.utils.getProp
import com.luckyzyx.luckytool.utils.getVersionCode
import com.luckyzyx.luckytool.utils.getVersionName
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.restartMain
import com.luckyzyx.luckytool.utils.setupMenuProvider

@Obfuscate
class HomeFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentHomeBinding
    private var homeFuncController: IGlobalFuncController? = null

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
                binding.updateView.apply {
                    isVisible = true
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
//        }

        binding.fpsTitle.text = getString(R.string.fps_title)
        binding.fpsSummary.text = getString(R.string.fps_summary)
        binding.fps.setOnClickListener {
            navigatePage(R.id.action_nav_home_to_forceFpsFragment, getString(R.string.fps_title))
        }
//        binding.fps.setOnLongClickListener {
//            navigatePage(R.id.action_nav_home_to_mainPrefsFragment)
//            true
//        }

        binding.systemInfo.apply {
            setOnLongClickListener {
                val binding = DialogOplusotaLayoutBinding.inflate(layoutInflater)
                MaterialAlertDialogBuilder(context, dialogCentered).apply {
                    setTitle("OPLUS OTA")
                    setView(binding.root)
                }.show()
                val productModel = binding.oplusotaProductModel.apply {
                    setText(getProp("ro.product.name"))
                    setOnLongClickListener {
                        context.copyStr(text as CharSequence)
                        true
                    }
                }
                val otaVersion = binding.oplusotaOtaVersion.apply {
                    setText(getProp("ro.build.version.ota"))
                    setOnLongClickListener {
                        context.copyStr(text as CharSequence)
                        true
                    }
                }
                val nvIdentifier = binding.oplusotaNvIdentifier.apply {
                    setText(getProp("ro.build.oplus_nv_id"))
                    setOnLongClickListener {
                        context.copyStr(text as CharSequence)
                        true
                    }
                }
                val guid = binding.oplusotaGuid.apply {
                    setText(DeviceUtils.getGuid())
                    setOnLongClickListener {
                        context.copyStr(text as CharSequence)
                        true
                    }
                }
                val recruit = binding.oplusotaRecruit.apply {
                    setText(DeviceUtils.getRecruit())
                    setOnLongClickListener {
                        context.copyStr(text as CharSequence)
                        true
                    }
                }
                binding.oplusotaCopyall.apply {
                    setOnClickListener {
                        context.copyStr(
                            """
                                ro.product.name -> ${productModel.text}
                                ro.build.version.ota -> ${otaVersion.text}
                                ro.build.oplus_nv_id -> ${nvIdentifier.text}
                                guid -> ${guid.text}
                                recruit -> ${recruit.text}
                            """.trimIndent()
                        )
                    }
                }
                true
            }
        }

        binding.donateTvTitle.text = getString(R.string.donate_tv_title) + " by: 忆清鸣、luckyzyx"
        binding.donateTvView.apply {
            setOnClickListener {
                val url = if (isZh(requireActivity())) "https://docs.qq.com/doc/DS2ZDZlNIeUlpdlV1"
                else "https://luckyzyx.github.io/LuckyTool_Doc/en/donate"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            setOnLongClickListener {
                val donateList = arrayListOf<CharSequence>(
                    getString(R.string.qq),
                    getString(R.string.wechat),
                    getString(R.string.alipay),
//                    getString(R.string.donation_list)
                )
                if (!isZh(context)) {
                    donateList.add(3, getString(R.string.patreon))
//                    donateList.add(4, getString(R.string.paypal))
                }
                MaterialAlertDialogBuilder(context).apply {
                    setItems(donateList.toTypedArray()) { _, which ->
                        when (which) {
                            0 -> DonateUtils.showQRCode(context, Base64CodeUtils.qqCode)
                            1 -> DonateUtils.showQRCode(context, Base64CodeUtils.wechatCode)
                            2 -> DonateUtils.showQRCode(context, Base64CodeUtils.alipayCode)
                            3 -> if (!isZh(context)) startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.patreon.com/LuckyTool")
                                )
                            )/* else navigatePage(
                                R.id.action_nav_setting_to_donateFragment,
                                getString(R.string.donation_list)
                            )*/

//                            4 -> startActivity(
//                                Intent(
//                                    Intent.ACTION_VIEW, Uri.parse("https://paypal.me/luckyzyx")
//                                )
//                            )

//                            5 -> navigatePage(
//                                R.id.action_nav_setting_to_donateFragment,
//                                getString(R.string.donation_list)
//                            )
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

    private fun initSystemInfoView(funcController: IGlobalFuncController?) {
        scopeLife {
            val deviceInfo = withDefault { requireActivity().getDeviceInfo(funcController) }
            if (deviceInfo.isNotBlank()) {
                binding.systemInfoLoading.isVisible = false
                binding.systemInfo.apply {
                    gravity = Gravity.START
                    text = deviceInfo
                    isVisible = true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (homeFuncController == null) requireActivity().bindRootService(
            GlobalFuncControllerService::class.java, { _, iBinder ->
                homeFuncController = IGlobalFuncController.Stub.asInterface(iBinder)
                initSystemInfoView(homeFuncController)
            })
        else initSystemInfoView(homeFuncController)
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
        if (menuItem.itemId == 1) requireActivity().restartMain()
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
        binding.statusTitle.text = when {
            YukiHookAPI.Status.isXposedModuleActive && enableModule.not() -> getString(R.string.module_is_disabled)
            YukiHookAPI.Status.isXposedModuleActive -> getString(R.string.module_isactivated)
            else -> getString(R.string.module_notactive)
        }

        binding.statusSummary.apply {
            text = "${getString(R.string.module_version)}$getVersionName($getVersionCode)"
        }
    }
}