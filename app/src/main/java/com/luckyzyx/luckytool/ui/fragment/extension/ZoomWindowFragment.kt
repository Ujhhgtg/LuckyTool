package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.google.android.material.materialswitch.MaterialSwitch
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.databinding.FragmentZoomWindowApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoSwitchItemBinding
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.setupMenuProvider
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@Obfuscate
class ZoomWindowFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentZoomWindowApplistLayoutBinding
    private var zoomWindowAdapter: ZoomWindowAdapter? = null

    private var allAppDatas = ArrayList<AppInfo>()

    private var isShowSystemApp = false

    private val showSystemAppKey = "show_system_app_zoom_window"
    private val supportListKey = "zoom_window_support_list"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        isShowSystemApp = requireActivity().getBoolean(ModulePrefs, showSystemAppKey, false)
        binding = FragmentZoomWindowApplistLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.enableSwitch.apply {
            isVisible = false
        }

        binding.searchViewLayout.apply {
            hint = "Name / PackageName"
            isHintEnabled = true
            isHintAnimationEnabled = true
        }
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                zoomWindowAdapter?.getFilter?.filter(text)
            })
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener { loadData() }
        }

        if (allAppDatas.isEmpty()) loadData()
    }

    /**
     * 加载数据
     */
    private fun loadData() {
        scopeLife {
            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null
            allAppDatas.clear()

            val enableData = requireActivity().getStringSet(ModulePrefs, supportListKey, ArraySet())

            withDefault {
                val packageManager = requireActivity().packageManager
                allAppDatas = PackageUtils(packageManager).getInstalledAppInfos(0, isShowSystemApp)
            }

            binding.recyclerView.apply {
                zoomWindowAdapter = ZoomWindowAdapter(context, allAppDatas, enableData)
                adapter = zoomWindowAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }
            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.app_list_menu, menu)
        menu.findItem(R.id.show_system_app).isChecked = isShowSystemApp
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.show_system_app) {
            menuItem.isChecked = !menuItem.isChecked
            isShowSystemApp = menuItem.isChecked
            requireActivity().putBoolean(ModulePrefs, showSystemAppKey, isShowSystemApp)
            loadData()
        }
        return true
    }
}

@Obfuscate
class ZoomWindowAdapter(
    val context: Context, allAppInfos: ArrayList<AppInfo>, allEnableData: Set<String>?
) : RecyclerView.Adapter<ZoomWindowAdapter.ViewHolder>() {
    private val supportListKey = "zoom_window_support_list"

    private var allDatas = ArrayList<AppInfo>()
    private var filterDatas = ArrayList<AppInfo>()

    private var enabledAppData = ArrayList<String>()

    private var hasPermissions = true

    init {
        allDatas.clear()
        filterDatas.clear()
        enabledAppData.clear()

        if (allAppInfos.size <= 1) hasPermissions = false

        allDatas = allAppInfos.apply {
            sortBy { it.name }
        }

        val sortDatas = ArrayList<AppInfo>()
        allEnableData?.forEach { its ->
            val find = allDatas.find { it.packName == its }
            if (find != null) {
                enabledAppData.add(its)
                sortDatas.add(find)
            }
        }
        saveEnableList()

        allDatas.apply {
            removeIf { sortDatas.contains(it) }
            addAll(0, sortDatas.apply {
                sortBy { it.name }
            })
        }

        filterDatas = allDatas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutAppinfoSwitchItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = filterDatas[position]
        val appIcon = appInfo.icon
        val appName = appInfo.name
        val packName = appInfo.packName

        holder.appIcon.setImageDrawable(appIcon)
        holder.appName.text = appName
        holder.packName.text = packName
        holder.appInfoView.setOnClickListener(null)
        holder.switchview.setOnCheckedChangeListener(null)

        holder.switchview.isChecked = enabledAppData.contains(packName)
        holder.appInfoView.setOnClickListener {
            holder.switchview.performClick()
        }
        holder.switchview.setOnCheckedChangeListener { _, isChecked ->
            enabledAppData.remove(packName)
            if (isChecked) enabledAppData.add(packName)
            saveEnableList()
        }
    }

    override fun getItemCount(): Int = filterDatas.size

    val getFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            filterDatas = if (constraint.isBlank()) {
                allDatas
            } else {
                val filterlist = ArrayList<AppInfo>()
                allDatas.forEach {
                    if (it.name.lowercase().contains(constraint.toString().lowercase())
                        || it.packName.lowercase().contains(constraint.toString().lowercase())
                    ) filterlist.add(it)
                }
                filterlist
            }
            val filterResults = FilterResults()
            filterResults.values = filterDatas
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: FilterResults?) {
            filterDatas = results?.values as ArrayList<AppInfo>
            refreshDatas()
        }
    }

    private fun saveEnableList() {
        if (!hasPermissions) return
        context.putStringSet(ModulePrefs, supportListKey, enabledAppData.toSet())
        context.dataChannel("android").put(supportListKey, enabledAppData.toSet())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshDatas() {
        notifyDataSetChanged()
    }

    class ViewHolder(binding: LayoutAppinfoSwitchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val appInfoView: ConstraintLayout = binding.root
        val appIcon: ImageView = binding.appIcon
        val appName: TextView = binding.appName
        val packName: TextView = binding.packName
        val switchview: MaterialSwitch = binding.switchview
    }
}