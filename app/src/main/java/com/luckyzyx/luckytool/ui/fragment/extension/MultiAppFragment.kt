package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.ArraySet
import android.view.*
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.materialswitch.MaterialSwitch
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.databinding.DialogAppinfoSortFilterSheetBinding
import com.luckyzyx.luckytool.databinding.FragmentMutliAppApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoSwitchItemBinding
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.jumpMultiApp
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.setupMenuProvider
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@Obfuscate
class MultiAppFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentMutliAppApplistLayoutBinding
    private var multiAppAdapter: MultiAppAdapter? = null

    private var allAppInfos = ArrayList<AppInfo>()

    private val showSystemAppKey = "show_system_app_multi_app"
    private val supportListKey = "multi_app_custom_list"

    private lateinit var sortFilterBinding: DialogAppinfoSortFilterSheetBinding
    private lateinit var sortFilterBottomSheet: BottomSheetDialog
    private var isReverse = false
    private var sortMode = 0
    private var showSystemApp = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        showSystemApp = requireActivity().getBoolean(ModulePrefs, showSystemAppKey, false)
        binding = FragmentMutliAppApplistLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSortFilterBottomSheet()
        binding.searchViewLayout.apply {
            hint = "Name / PackageName"
            setEndIconOnClickListener {
                sortFilterBottomSheet.show()
            }
        }
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                multiAppAdapter?.getFilter?.filter(text)
            })
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                loadData()
            }
        }

        if (allAppInfos.isEmpty()) loadData()
    }

    private fun initSortFilterBottomSheet() {
        sortFilterBinding = DialogAppinfoSortFilterSheetBinding.inflate(layoutInflater)
        sortFilterBottomSheet = BottomSheetDialog(requireActivity()).apply {
            setContentView(sortFilterBinding.root)
        }
        sortFilterBinding.sortReverse.apply {
            setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                isReverse = isChecked
                loadData()
            }
        }
        sortFilterBinding.sortChips.apply {
            isSingleSelection = true
            arrayOf(
                getString(R.string.appinfo_app_name),
                getString(R.string.appinfo_package_name),
                getString(R.string.appinfo_app_size),
                getString(R.string.appinfo_install_time),
                getString(R.string.appinfo_last_updated_time),
                getString(R.string.appinfo_target_sdk)
            ).forEachIndexed { index, title ->
                addView(getSortChip(index, title))
            }
            setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
                sortMode = checkedIds.first() - 1
                loadData()
            }
        }
        sortFilterBinding.filterChips.apply {
            isSingleSelection = false
            arrayOf(getString(R.string.appinfo_system_app)).forEachIndexed { index, title ->
                addView(getFilterChip(index, title))
            }
        }
    }

    private fun getSortChip(index: Int, title: String): Chip {
        return Chip(requireActivity()).apply {
            text = title
            isCheckable = true
            isClickable = true
            isChecked = index == 0
        }
    }

    private fun getFilterChip(index: Int, title: String): Chip {
        return Chip(requireActivity()).apply {
            text = title
            isCheckable = true
            isClickable = true
            when (index) {
                0 -> {
                    isChecked = showSystemApp
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                        showSystemApp = isChecked
                        context.putBoolean(ModulePrefs, showSystemAppKey, showSystemApp)
                        loadData()
                    }
                }
            }
        }
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
                    val find = allAppInfos.find { it.packName == its }
                    if (find != null) enableInfos.add(find)
                }
                allAppInfos.apply {
                    when (sortMode) {
                        0 -> sortBy { it.name }
                        1 -> sortBy { it.packName }
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
                        1 -> sortBy { it.packName }
                        2 -> sortBy { it.size }
                        3 -> sortBy { it.installTime }
                        4 -> sortBy { it.lastInstallTime }
                        5 -> sortBy { it.target }
                    }
                    if (isReverse) reverse()
                }
            }

            binding.recyclerView.apply {
                multiAppAdapter = MultiAppAdapter(context, allAppInfos, enableInfos)
                adapter = multiAppAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }

            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.common_words_open)).apply {
            setIcon(R.drawable.baseline_open_in_new_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == 1) jumpMultiApp(requireActivity())
        return true
    }
}

@Obfuscate
class MultiAppAdapter(
    val context: Context, allAppInfos: ArrayList<AppInfo>, allEnableInfos: ArrayList<AppInfo>
) : RecyclerView.Adapter<MultiAppAdapter.ViewHolder>() {
    private val supportListKey = "multi_app_custom_list"

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
            enabledAppData.add(it.packName)
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
            filterDatas = if (constraint.isBlank()) allDatas
            else {
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