package com.luckyzyx.luckytool.ui.fragment.extension

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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.drake.net.utils.scopeLife
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IRefreshRateController
import com.luckyzyx.luckytool.databinding.FragmentFpsBinding
import com.luckyzyx.luckytool.service.controller.RefreshRateControllerService
import com.luckyzyx.luckytool.utils.DisplayMode
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsAutoStart
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsCur
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsMode
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getFpsMode1
import com.luckyzyx.luckytool.utils.getInt
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putInt

@Obfuscate
class ForceFpsFragment : Fragment() {

    private lateinit var binding: FragmentFpsBinding
    private var controller: IRefreshRateController? = null

    private var allData = java.util.ArrayList<Any?>()
    private var idData = ArrayList<Int>()
    private var fpsData = ArrayList<String>()
    private var fpsAdapter: ArrayAdapter<String>? = null

    private var fpsAutostart = false
    private var fpsMode = 1
    private var fpsCur = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFpsBinding.inflate(inflater)
        return binding.root
    }

    fun init(context: Context) {
        binding.swipeRefreshLayout.isRefreshing = true
        binding.swipeRefreshLayout.setOnRefreshListener { init(requireActivity()) }
        if (controller == null) return
        scopeLife {
            clearAllData()
            fpsMode = context.getInt(SettingsPrefs, keyFpsMode, 1)
            allData = if (fpsMode == 1) getFpsMode1()
            else controller?.supportModes as java.util.ArrayList<Any?>
            initFpsData(allData)
            fpsCur = context.getInt(SettingsPrefs, keyFpsCur, -1)
            fpsAutostart = context.getBoolean(SettingsPrefs, keyFpsAutoStart, false)
            fpsAdapter = ArrayAdapter(
                context, android.R.layout.simple_list_item_single_choice, fpsData
            )
            val isUnsupport = allData.isEmpty()
            val fpsSelfStart = binding.fpsSelfStart.apply {
                isChecked = fpsAutostart
                isEnabled = !isUnsupport && fpsCur != -1
                setOnCheckedChangeListener { _, isChecked ->
                    context.updateAutoStatus(isChecked)
                }
            }
            binding.fpsNodataView.isVisible = isUnsupport
            binding.fpsList.apply {
                isVisible = !isUnsupport
                choiceMode = ListView.CHOICE_MODE_SINGLE
                if (!isUnsupport) adapter = fpsAdapter
                val curFpsId = idData.indexOf(fpsCur)
                if (curFpsId != -1) setItemChecked(curFpsId, fpsCur != -1)
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    fpsSelfStart.isEnabled = true
                    val id = idData[position]
                    context.updateRefreshRateMode(id)
                    if (fpsMode == 2) controller?.setRefreshRateMode(id)
                }
            }
            binding.fpsMode1.apply {
                isEnabled = true
                if (fpsMode == 1) toggle()
                setOnCheckedChangeListener { btn, _ ->
                    if (btn.isPressed.not()) return@setOnCheckedChangeListener
                    fpsSelfStart.isChecked = false
                    fpsSelfStart.isEnabled = false
                    context.changeFpsMode(1)
                }
            }
            binding.fpsMode2.apply {
                isEnabled = true
                if (fpsMode == 2) toggle()
                setOnCheckedChangeListener { btn, _ ->
                    if (btn.isPressed.not()) return@setOnCheckedChangeListener
                    fpsSelfStart.isChecked = false
                    fpsSelfStart.isEnabled = false
                    context.changeFpsMode(2)
                }
            }
            binding.fpsShow.apply {
                isEnabled = controller != null
                isChecked = controller?.refreshRateDisplay == true
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView.isPressed) controller?.refreshRateDisplay = isChecked
                }
            }
            binding.fpsRecover.apply {
                isEnabled = controller != null
                isVisible = fpsMode == 2
                setOnClickListener {
                    fpsSelfStart.isChecked = false
                    fpsSelfStart.isEnabled = false
                    context.changeFpsMode(-1)
                }
            }
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (controller == null) requireActivity().bindRootService(
            RefreshRateControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IRefreshRateController.Stub.asInterface(iBinder)
                init(requireActivity())
            })
        else init(requireActivity())
    }

    private fun Context.changeFpsMode(mode: Int) {
        if (mode != -1) updateFpsMode(mode)
        updateRefreshRateMode(-1)
        controller?.resetRefreshRateMode()
        init(this)
    }

    private fun clearAllData() {
        allData.clear()
        idData.clear()
        fpsData.clear()
    }

    private fun initFpsData(allData: java.util.ArrayList<Any?>) {
        allData.forEachIndexed { index, any ->
            val fps = any?.let { (it as DisplayMode) } ?: return@forEachIndexed
            idData.add(index)
            if (fpsMode == 1) fpsData.add("${fps.id}                 ${fps.refreshRate}")
            else fpsData.add("${fps.id}   ${fps.width} x ${fps.height}   ${fps.refreshRate}")
        }
    }

    private fun Context.updateFpsMode(mode: Int) {
        putInt(SettingsPrefs, keyFpsMode, mode)
        dataChannel("com.android.systemui").put(keyFpsMode, mode)
    }

    private fun Context.updateAutoStatus(isChecked: Boolean) {
        putBoolean(SettingsPrefs, keyFpsAutoStart, isChecked)
        dataChannel("com.android.systemui").put(keyFpsAutoStart, isChecked)
    }

    private fun Context.updateRefreshRateMode(mode: Int) {
        putInt(SettingsPrefs, keyFpsCur, mode)
        dataChannel("com.android.systemui").put(keyFpsCur, mode)
    }
}