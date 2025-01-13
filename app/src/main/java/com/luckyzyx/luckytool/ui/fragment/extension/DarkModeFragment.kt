package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.ArrayMap
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.google.android.material.chip.Chip
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.data.DarkModeInfo
import com.luckyzyx.luckytool.databinding.FragmentDarkModeApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoSwitchItemDarkmodeBinding
import com.luckyzyx.luckytool.selector.SortFilterSelector
import com.luckyzyx.luckytool.utils.IntentUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.RestartMenuUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.setupMenuProvider
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@Obfuscate
class DarkModeFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentDarkModeApplistLayoutBinding
    private var darkModeAdapter: DarkModeAdapter? = null

    private var allAppInfos = ArrayList<AppInfo>()

    private val scopes = arrayOf("com.android.settings")

    private val enableSwitchKey = "dark_mode_list_enable"
    private val showSystemAppKey = "show_system_app_dark_mode"
    private val supportListKey = "dark_mode_support_list"

    private var isReverse = false
    private var sortMode = 0
    private var showSystemApp = false

    private lateinit var sortFilterSelector: SortFilterSelector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        showSystemApp = requireActivity().getBoolean(ModulePrefs, showSystemAppKey, false)
        binding = FragmentDarkModeApplistLayoutBinding.inflate(inflater)
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
                setOnCheckedChangeListener { _, isChecked ->
                    showSystemApp = isChecked
                    context.putBoolean(ModulePrefs, showSystemAppKey, showSystemApp)
                    loadData()
                }
            }))
        }
        binding.enableSwitch.apply {
            text = context.getString(R.string.enable_dark_mode_list)
            isChecked = context.getBoolean(ModulePrefs, enableSwitchKey, false)
            setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    context.putBoolean(ModulePrefs, enableSwitchKey, isChecked)
                    context.dataChannel("android").put(enableSwitchKey, isChecked)
                }
            }
        }

        binding.searchViewLayout.apply {
            hint = "Name / PackageName"
            setEndIconOnClickListener {
                sortFilterSelector.show()
            }
        }
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                darkModeAdapter?.getFilter?.filter(text)
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
                .toMutableList()

            val enabledDarkMode = ArrayList<DarkModeInfo>()
            withDefault {
                val packageManager = requireActivity().packageManager
                allAppInfos = PackageUtils(packageManager).getInstalledAppInfos(0)
                enableData.forEach { its ->
                    val darkModeInfo = DarkModeInfo().toDarkModeInfo(its) ?: return@forEach
                    val find = allAppInfos.find { it.packageName == darkModeInfo.packName }
                    if (find != null) enabledDarkMode.add(darkModeInfo)
                }
                allAppInfos.apply {
                    if (!showSystemApp) removeIf { it.isSystem }
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
                enabledDarkMode.apply {
                    when (sortMode) {
                        1 -> sortBy { it.packName }
                    }
                    if (isReverse) reverse()
                }
            }

            binding.recyclerView.apply {
                darkModeAdapter = DarkModeAdapter(context, allAppInfos, enabledDarkMode)
                adapter = darkModeAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }

            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.menu_reboot)).apply {
            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 2, 0, getString(R.string.common_words_open)).apply {
            setIcon(R.drawable.baseline_open_in_new_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == 1) RestartMenuUtils.showRestartScopeDialog(requireActivity(), scopes)
        if (menuItem.itemId == 2) IntentUtils(requireActivity()).jumpDarkMode()
        return true
    }
}

@Obfuscate
class DarkModeAdapter(
    val context: Context, allAppInfos: ArrayList<AppInfo>, allEnableInfos: ArrayList<DarkModeInfo>
) : RecyclerView.Adapter<DarkModeAdapter.ViewHolder>() {
    private val supportListKey = "dark_mode_support_list"

    private var allDatas = ArrayList<AppInfo>()
    private var filterDatas = ArrayList<AppInfo>()

    private var enabledAppData = ArrayMap<String, DarkModeInfo>()

    private var hasPermissions = true

    init {
        allDatas.clear()
        filterDatas.clear()
        enabledAppData.clear()

        if (allAppInfos.size <= 1) hasPermissions = false

        allDatas = allAppInfos
        filterDatas = allDatas

        val sortDatas = ArrayList<AppInfo>()
        allEnableInfos.forEach { its ->
            val find = allDatas.find { it.packageName == its.packName } ?: return@forEach
            enabledAppData[its.packName] = its
            sortDatas.add(find)
            allDatas.remove(find)
        }
        allDatas.addAll(0, sortDatas)

        saveEnableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutAppinfoSwitchItemDarkmodeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = filterDatas[position]
        val appIcon = appInfo.icon
        val appName = appInfo.name
        val packName = appInfo.packageName
        val data = enabledAppData[packName]

        holder.appIcon.setImageDrawable(appIcon)
        holder.appName.text = appName
        holder.packName.text = packName
        holder.appInfoView.setOnClickListener(null)
        holder.switchview.setOnCheckedChangeListener(null)
        holder.sliderview.clearOnChangeListeners()

        holder.switchview.isChecked = data != null
        holder.sliderLayout.isVisible = data != null
        holder.sliderview.value = data?.curType?.toFloat() ?: 0F

        holder.appInfoView.setOnClickListener {
            holder.switchview.performClick()
        }
        holder.switchview.setOnCheckedChangeListener { _, isChecked ->
            enabledAppData.remove(packName)
            holder.sliderLayout.isVisible = isChecked
            if (isChecked) {
                enabledAppData[packName] = DarkModeInfo(packName)
                holder.sliderview.value = 0F
            }
            saveEnableList()
        }
        holder.sliderview.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            enabledAppData[packName]?.curType = value.toInt()
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
        val data = ArraySet<String>()
        enabledAppData.forEach {
            data.add(it.value.toJSONObject().toString())
        }
        context.putStringSet(ModulePrefs, supportListKey, data.toSet())
        context.dataChannel("android").put(supportListKey, data.toSet())
        context.dataChannel("com.android.settings").put(supportListKey, data.toSet())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshDatas() {
        notifyDataSetChanged()
    }

    class ViewHolder(binding: LayoutAppinfoSwitchItemDarkmodeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val appInfoView: ConstraintLayout = binding.appinfoView
        val appIcon: ImageView = binding.appIcon
        val appName: TextView = binding.appName
        val packName: TextView = binding.packName
        val switchview: MaterialSwitch = binding.switchview
        val sliderLayout: LinearLayout = binding.sliderLayout
        val sliderview: Slider = binding.slider
    }
}