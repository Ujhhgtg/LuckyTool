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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.google.android.material.chip.Chip
import com.google.android.material.materialswitch.MaterialSwitch
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.databinding.FragmentZoomWindowApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoSwitchItemBinding
import com.luckyzyx.luckytool.selector.SortFilterSelector
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.setupMenuProvider
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@Obfuscate
class ZoomWindowFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentZoomWindowApplistLayoutBinding
    private var zoomWindowAdapter: ZoomWindowAdapter? = null

    private var allAppInfos = ArrayList<AppInfo>()

    private val showSystemAppKey = "show_system_app_zoom_window"
    private val supportListKey = "zoom_window_support_list"

    private var isReverse = false
    private var sortMode = 0
    private var showSystemApp = false

    private lateinit var sortFilterSelector: SortFilterSelector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        showSystemApp = requireActivity().getBoolean(ModulePrefs, showSystemAppKey, false)
        binding = FragmentZoomWindowApplistLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sortFilterSelector = SortFilterSelector(requireActivity()).apply {
            setReverse(true) { _, isChecked ->
                isReverse = isChecked
                loadData()
            }
            setSortChips(
                true, context.resources.getStringArray(R.array.sort_selector_chips)
            ) { _, checkedIds ->
                sortMode = checkedIds.firstOrNull() ?: 0
                loadData()
            }
            setFilterChips(true, arrayOf(Chip(context).apply {
                text = context.getString(R.string.appinfo_system_app)
                isCheckable = true
                isClickable = true
                isChecked = showSystemApp
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                    showSystemApp = isChecked
                    loadData()
                }
            }))
        }
        binding.searchViewLayout.apply {
            hint = "Name / PackageName"
            setEndIconOnClickListener {
                sortFilterSelector.show()
            }
        }
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                zoomWindowAdapter?.getFilter?.filter(text)
            })
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener { loadData() }
        }

        if (allAppInfos.isEmpty()) loadData()
    }

    private fun loadData() {
        scopeLife {
            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null
            allAppInfos.clear()

            val enableData = requireActivity().getStringSet(ModulePrefs, supportListKey, ArraySet())
                ?.toMutableList() ?: mutableListOf()

            val enableInfos = ArrayList<AppInfo>()
            withDefault {
                val packageManager = requireActivity().packageManager
                allAppInfos = PackageUtils(packageManager).getInstalledAppInfos(0, showSystemApp)
                enableData.forEach { its ->
                    val find = allAppInfos.find { it.packageName == its }
                    if (find != null) enableInfos.add(find)
                }
                allAppInfos.apply {
                    when (sortMode) {
                        0 -> sortBy { it.name }
                        1 -> sortBy { it.packageName }
                        2 -> sortBy { it.size }
                        3 -> sortBy { it.installTime }
                        4 -> sortBy { it.lastInstallTime }
                        5 -> sortBy { it.target }
                    }
                    if (isReverse) reverse()
                }
                enableInfos.apply {
                    when (sortMode) {
                        0 -> sortBy { it.name }
                        1 -> sortBy { it.packageName }
                        2 -> sortBy { it.size }
                        3 -> sortBy { it.installTime }
                        4 -> sortBy { it.lastInstallTime }
                        5 -> sortBy { it.target }
                    }
                    if (isReverse) reverse()
                }
            }

            binding.recyclerView.apply {
                zoomWindowAdapter = ZoomWindowAdapter(context, allAppInfos, enableInfos)
                adapter = zoomWindowAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }

            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }
}

@Obfuscate
class ZoomWindowAdapter(
    val context: Context, allAppInfos: ArrayList<AppInfo>, allEnableInfos: ArrayList<AppInfo>
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

        allDatas = allAppInfos
        filterDatas = allDatas

        allEnableInfos.forEach {
            enabledAppData.add(it.packageName)
            allDatas.remove(it)
        }
        allDatas.addAll(0, allEnableInfos)
        saveEnableList()
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
        val packName = appInfo.packageName

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
            val filterStr = constraint.toString().lowercase()
            filterDatas = if (constraint.isBlank()) allDatas
            else {
                val filterlist = ArrayList<AppInfo>()
                allDatas.forEach {
                    if (it.name.lowercase().contains(filterStr)
                        || it.packageName.lowercase().contains(filterStr)
                    ) filterlist.add(it)
                }
                filterlist
            }
            val filterResults = FilterResults()
            filterResults.values = filterDatas
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filterDatas = results.values as ArrayList<AppInfo>
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