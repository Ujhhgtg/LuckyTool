package com.luckyzyx.luckytool.ui.fragment.others

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IAdbDebugController
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.FragmentOtherBinding
import com.luckyzyx.luckytool.databinding.LayoutAdbDialogBinding
import com.luckyzyx.luckytool.databinding.LayoutShortcutDialogBinding
import com.luckyzyx.luckytool.service.controller.AdbDebugControllerService
import com.luckyzyx.luckytool.utils.OtherPrefs
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.ShortcutUtils
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.copyStr
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.putString

@Obfuscate
class OtherFragment : Fragment() {

    private lateinit var binding: FragmentOtherBinding
    private var adbController: IAdbDebugController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtherBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun init(context: Context) {
        binding.quickEntry.setOnClickListener {
            navigatePage(R.id.action_nav_other_to_systemQuickEntry, getString(R.string.quick_entry))
        }

        binding.shortcut.apply {
            setOnClickListener {
                val binding = LayoutShortcutDialogBinding.inflate(layoutInflater)
                MaterialAlertDialogBuilder(context, dialogCentered).apply {
                    setView(binding.root)
                }.show()
                val shortcutList = ShortcutUtils(context).getShortcutList()
                val keys = ArrayList<String>(shortcutList.keys)
                val titles = ArrayList<String>(shortcutList.values)
                binding.shortcutList.apply {
                    choiceMode = ListView.CHOICE_MODE_MULTIPLE
                    adapter = ArrayAdapter(
                        context, android.R.layout.simple_list_item_multiple_choice, titles
                    )
                    keys.forEach {
                        if (context.getBoolean(SettingsPrefs, it, false)) {
                            val index = keys.indexOf(it)
                            if (index != -1) setItemChecked(index, true)
                        }
                    }
                    onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
                        val key = keys[position]
                        val isChecked = (view as AppCompatCheckedTextView).isChecked
                        ShortcutUtils(context).setShortcutStatus(key, isChecked)
                        ShortcutUtils(context).setDynamicShortcuts()
                    }
                }
            }
        }

        binding.remoteAdbDebug.apply {
            isVisible = adbController != null
            setOnClickListener {
                val getPort = adbController?.adbPort ?: return@setOnClickListener
                var getIP = adbController?.wifiIP ?: "IP"

                val binding = LayoutAdbDialogBinding.inflate(layoutInflater)
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
                    isEnabled = adbController != null
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
                                    adbController?.adbPort = port
                                    adbController?.restartAdb()
                                    getIP = adbController?.wifiIP ?: "IP"
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
                                adbController?.adbPort = -1
                                adbController?.restartAdb()
                                adbController?.adbPort = 0
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
        if (adbController == null) requireActivity().bindRootService(
            AdbDebugControllerService::class.java, { _: ComponentName?, iBinder: IBinder? ->
                adbController = IAdbDebugController.Stub.asInterface(iBinder)
                init(requireActivity())
            })
        else init(requireActivity())
    }
}