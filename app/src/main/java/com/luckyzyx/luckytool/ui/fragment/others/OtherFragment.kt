package com.luckyzyx.luckytool.ui.fragment.others

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.util.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IAdbDebugController
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogAdbLayoutBinding
import com.luckyzyx.luckytool.databinding.FragmentOtherBinding
import com.luckyzyx.luckytool.service.AdbService
import com.luckyzyx.luckytool.service.TilesService
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyTouchSamplingRateLevel
import com.luckyzyx.luckytool.utils.OtherPrefs
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.ShortcutUtils
import com.luckyzyx.luckytool.utils.copyStr
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.putString

@Obfuscate
class OtherFragment : Fragment() {

    private lateinit var binding: FragmentOtherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherBinding.inflate(inflater)
        return binding.root
    }

    fun init(context: Context) {
        binding.quickEntry.setOnClickListener {
            navigatePage(R.id.systemQuickEntry, getString(R.string.quick_entry))
        }

        binding.shortcut.apply {
            setOnClickListener {
                val keys = ArrayList<String>()
                val titles = ArrayList<CharSequence>()
                val values = ArrayList<Boolean>()
                ShortcutUtils(context).getDefaultShortcutList().forEachIndexed { _, bean ->
                    if (bean.label.isNotBlank() && bean.key.isNotBlank()) {
                        keys.add(bean.key)
                        titles.add(bean.label)
                        values.add(bean.isEnable)
                    }
                }
                MaterialAlertDialogBuilder(context, dialogCentered).apply {
                    setTitle(binding.shortcutTitle.text)
                    setMultiChoiceItems(titles.toTypedArray(), values.toBooleanArray(), null)
                    setPositiveButton(android.R.string.ok) { dialog, _ ->
                        val positions = (dialog as AlertDialog).listView.checkedItemPositions
                        positions.forEach { position, isChecked ->
                            val key = keys[position]
                            ShortcutUtils(context).setShortcutStatus(key, isChecked)
                        }
                        ShortcutUtils(context).updateDynamicShortcuts()
                    }
                    setNeutralButton(android.R.string.cancel, null)
                }.show()
            }
        }

        binding.fpsTitle.text = getString(R.string.fps_title)
        binding.fpsSummary.text = getString(R.string.fps_summary)
        binding.fps.setOnClickListener {
            navigatePage(R.id.forceFpsFragment, getString(R.string.fps_title))
        }
    }

    private fun initTouchPanelView() {
        val touchs = arrayOf("120", "180", "240", "360", "480", "600", "720")
        TilesService.get(activity) { controller ->
            binding.touchPanel.apply {
                isVisible = controller != null && controller.checkTouchMode()
                setOnClickListener {
                    val curLevel =
                        context.getString(SettingsPrefs, keyTouchSamplingRateLevel, "240")
                    MaterialAlertDialogBuilder(context, dialogCentered).apply {
                        setTitle(binding.touchTitle.text)
                        setSingleChoiceItems(touchs, touchs.indexOf(curLevel), null)
                        setPositiveButton(android.R.string.ok) { dialog, _ ->
                            val position = (dialog as AlertDialog).listView.checkedItemPosition
                            val value = if (position > 0) touchs[position] else position.toString()
                            context.putString(SettingsPrefs, keyTouchSamplingRateLevel, value)
                            controller?.touchMode = value.toInt()
                        }
                        setNeutralButton(android.R.string.cancel, null)
                    }.show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initAdbDebugView() {
        var controller: IAdbDebugController? = null
        AdbService.get(activity) {
            controller = it
        }
        binding.remoteAdbDebug.apply {
            isVisible = controller != null
            setOnClickListener {
                val getPort = controller?.adbPort ?: return@setOnClickListener
                var getIP = controller?.wifiIP ?: "IP"

                val binding = DialogAdbLayoutBinding.inflate(layoutInflater)
                MaterialAlertDialogBuilder(context).apply {
                    setCancelable(true)
                    setView(binding.root)
                }.show()
                val adbPortLayout = binding.adbPortLayout
                val adbPort = binding.adbPort.apply {
                    setText(
                        if (getPort == 0 || getPort == -1) {
                            context.getString(OtherPrefs, "adb_port", "6666")
                        } else getPort.toString()
                    )
                }
                val adbTv = binding.adbTv.apply {
                    if (getPort != 0 && getPort != -1) text = "adb connect $getIP:$getPort"
                    setOnLongClickListener {
                        context.copyStr(text.toString())
                        true
                    }
                }
                val adbTvTip = binding.adbTvTip.apply {
                    isVisible = adbTv.text.isNullOrBlank().not()
                    setOnLongClickListener {
                        context.copyStr(adbTv.text.toString())
                        true
                    }
                }
                binding.adbSwitch.apply {
                    isChecked = isEnabled && getPort != 0 && getPort != -1
                    adbPortLayout.isEnabled = isChecked.not()
                    setOnCheckedChangeListener { buttonView, checked ->
                        if (!buttonView.isPressed) return@setOnCheckedChangeListener
                        if (checked) {
                            val portStr = adbPort.text
                            if (portStr.isNullOrBlank()) {
                                isChecked = false
                                adbTv.text = context.getString(R.string.adb_debug_port_cannot_null)
                                return@setOnCheckedChangeListener
                            }
                            scopeLife {
                                val port = portStr.toString().toInt()
                                isEnabled = false
                                withDefault {
                                    controller?.adbPort = port
                                    controller?.restartAdb()
                                    getIP = controller?.wifiIP ?: "IP"
                                    context.putString(OtherPrefs, "adb_port", port.toString())
                                }
                                adbPortLayout.isEnabled = false
                                adbTv.text = "adb connect $getIP:$portStr"
                                adbTvTip.isVisible = true
                                isEnabled = true
                            }
                        } else scopeLife {
                            isEnabled = false
                            withDefault {
                                controller?.adbPort = -1
                                controller?.restartAdb()
                                controller?.adbPort = 0
                            }
                            adbPortLayout.isEnabled = true
                            adbTv.text = ""
                            adbTvTip.isVisible = false
                            isEnabled = true
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        initAdbDebugView()
        initTouchPanelView()
    }
}