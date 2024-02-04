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
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IRefreshRateController
import com.luckyzyx.luckytool.databinding.FragmentFpsBinding
import com.luckyzyx.luckytool.service.controller.RefreshRateControllerService
import com.luckyzyx.luckytool.utils.DisplayMode
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsAutoStart
import com.luckyzyx.luckytool.utils.GlobalKeyValue.keyFpsCur
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getInt
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putInt

@Obfuscate
class ForceFpsFragment : Fragment() {

    private lateinit var binding: FragmentFpsBinding
    private var controller: IRefreshRateController? = null

    private var allRefreshData = ArrayList<DisplayMode>()
    private var fpsIds = ArrayList<Int>()
    private var fpsDatas = ArrayList<String>()

    private var fpsAdapter: ArrayAdapter<String>? = null

    private var fpsAutostart = false
    private var fpsCur = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFpsBinding.inflate(inflater)
        return binding.root
    }

    fun init(context: Context, controller: IRefreshRateController?) {
        if (controller == null) return
        scopeLife {
            binding.swipeRefreshLayout.isRefreshing = true

            clearAllData()
            @Suppress("UNCHECKED_CAST")
            allRefreshData =
                (this@ForceFpsFragment.controller?.supportModes
                    ?: ArrayList<DisplayMode>()) as ArrayList<DisplayMode>
            initFpsData(allRefreshData)

            fpsCur = context.getInt(SettingsPrefs, keyFpsCur, -1)
            fpsAutostart = context.getBoolean(SettingsPrefs, keyFpsAutoStart, false)
            fpsAdapter = ArrayAdapter(
                context, android.R.layout.simple_list_item_single_choice, fpsDatas
            )
            val isUnsupport = allRefreshData.isEmpty()
            val fpsSelfStart = binding.fpsSelfStart.apply {
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
                if (!isUnsupport) adapter = fpsAdapter
                val curFpsId = fpsIds.indexOf(fpsCur)
                if (curFpsId != -1) setItemChecked(curFpsId, fpsCur != -1)
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    fpsSelfStart.isEnabled = true
                    val id = fpsIds[position]
                    context.putInt(SettingsPrefs, keyFpsCur, id)
                    this@ForceFpsFragment.controller?.setRefreshRateMode(id)
                }
            }
            binding.fpsShow.apply {
                isEnabled = this@ForceFpsFragment.controller != null
                isChecked = this@ForceFpsFragment.controller?.refreshRateDisplay == true
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView.isPressed) this@ForceFpsFragment.controller?.refreshRateDisplay =
                        isChecked
                }
            }
            binding.fpsRecover.apply {
                isEnabled = this@ForceFpsFragment.controller != null
                setOnClickListener {
                    fpsSelfStart.isChecked = false
                    fpsSelfStart.isEnabled = false
                    context.resetRefreshRate()
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.swipeRefreshLayout.setOnRefreshListener { init(requireActivity(), controller) }

        if (controller == null) requireActivity().bindRootService(
            RefreshRateControllerService::class.java,
            { _: ComponentName?, iBinder: IBinder? ->
                controller = IRefreshRateController.Stub.asInterface(iBinder)
                init(requireActivity(), controller)
            })
        else init(requireActivity(), controller)
    }

    private fun Context.resetRefreshRate() {
        putInt(SettingsPrefs, keyFpsCur, id)
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