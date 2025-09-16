package com.luckyzyx.luckytool.ui.fragment.extension

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.CheckedTextView
import android.widget.ListView
import androidx.core.view.isVisible
import com.drake.net.utils.scopeLife
import com.highcapable.betterandroid.ui.component.adapter.factory.bindAdapter
import com.luckyzyx.luckytool.IRefreshRateController
import com.luckyzyx.luckytool.data.DisplayMode
import com.luckyzyx.luckytool.databinding.FragmentFpsBinding
import com.luckyzyx.luckytool.service.RefreshRateService
import com.luckyzyx.luckytool.ui.fragment.base.BaseFragment
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsAutoStart
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsCur
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getInt
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putInt
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class ForceFpsFragment : BaseFragment<FragmentFpsBinding>() {

    private var controller: IRefreshRateController? = null

    private var allRefreshData = ArrayList<DisplayMode>()
    private var fpsIds = ArrayList<Int>()
    private var fpsDatas = ArrayList<String>()

    private var fpsAutostart = false
    private var fpsCur = -1

    fun init(context: Context, controller: IRefreshRateController?) {
        scopeLife {

            clearAllData()
            @Suppress("UNCHECKED_CAST")
            allRefreshData =
                (controller?.supportModes ?: ArrayList<DisplayMode>()) as ArrayList<DisplayMode>
            initFpsData(allRefreshData)

            fpsCur = context.getInt(SettingsPrefs, keyFpsCur, -1)
            fpsAutostart = context.getBoolean(SettingsPrefs, keyFpsAutoStart, false)

            val isUnsupport = allRefreshData.isEmpty()
            val fpsSelfStart = binding.fpsSelfStart.apply {
                isEnabled = controller != null
                isChecked = fpsAutostart
                isEnabled = !isUnsupport && fpsCur != -1
                setOnCheckedChangeListener { _, isChecked ->
                    context.putBoolean(SettingsPrefs, keyFpsAutoStart, isChecked)
                }
            }
            binding.fpsNodataView.isVisible = isUnsupport
            binding.fpsList.apply {
                isVisible = !isUnsupport
                choiceMode = ListView.CHOICE_MODE_SINGLE
                bindAdapter<String> {
                    onBindData { fpsDatas }
                    onBindItemView(android.R.layout.simple_list_item_single_choice) { view, data, position ->
                        (view as CheckedTextView).text = data
                    }
                }
                val curFpsId = fpsIds.indexOf(fpsCur)
                if (curFpsId != -1) setItemChecked(curFpsId, fpsCur != -1)
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    fpsSelfStart.isEnabled = true
                    val curId = fpsIds[position]
                    context.putInt(SettingsPrefs, keyFpsCur, curId)
                    controller?.setRefreshRateMode(curId)
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
                setOnClickListener {
                    fpsSelfStart.isChecked = false
                    fpsSelfStart.isEnabled = false
                    context.resetRefreshRate()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RefreshRateService.get(activity) {
            controller = it
            init(requireActivity(), controller)
        }
    }

    private fun Context.resetRefreshRate() {
        putInt(SettingsPrefs, keyFpsCur, -1)
        controller?.resetRefreshRateMode()
        init(this, controller)
    }

    private fun clearAllData() {
        allRefreshData.clear()
        fpsIds.clear()
        fpsDatas.clear()
    }

    private fun initFpsData(allData: ArrayList<DisplayMode>) {
        allData.forEachIndexed { index, fps ->
            fpsIds.add(index)
            fpsDatas.add("${fps.id}   ${fps.width} x ${fps.height}   ${fps.refreshRate}")
        }
    }
}