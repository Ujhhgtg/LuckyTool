package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.ArrayMap
import android.util.ArraySet
import android.view.*
import android.widget.*
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
import com.google.android.material.slider.Slider
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.FragmentDarkModeApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoSwitchItemDarkmodeBinding
import com.luckyzyx.luckytool.utils.*
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@Obfuscate
class DarkModeFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentDarkModeApplistLayoutBinding
    private var darkModeAdapter: DarkModeAdapter? = null

    private var allAppDatas = ArrayList<AppInfo>()

    private val scopes = arrayOf("com.android.settings")

    private var isShowSystemApp = false

    private val enableSwitchKey = "dark_mode_list_enable"
    private val showSystemAppKey = "show_system_app_dark_mode"
    private val supportListKey = "dark_mode_support_list"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        isShowSystemApp = requireActivity().getBoolean(ModulePrefs, showSystemAppKey, false)
        binding = FragmentDarkModeApplistLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            isHintEnabled = true
            isHintAnimationEnabled = true
        }
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                darkModeAdapter?.getFilter?.filter(text)
            })
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener { loadData() }
        }

        if (allAppDatas.isEmpty()) loadData()
    }

    private fun loadData() {
        scopeLife {
            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null
            allAppDatas.clear()

            val enableData = requireActivity().getStringSet(ModulePrefs, supportListKey, ArraySet())
            val enabledDarkMode = ArrayList<DarkModeInfo>()

            enableData?.forEach {
                val darkModeInfo = DarkModeInfo().toDarkModeInfo(it)
                if (darkModeInfo != null) enabledDarkMode.add(darkModeInfo)
            }

            withDefault {
                val packageManager = requireActivity().packageManager
                val appinfos = PackageUtils(packageManager).getInstalledApplications(0)
                for (i in appinfos) {
                    if (i.flags and ApplicationInfo.FLAG_SYSTEM == 1 && !isShowSystemApp) continue
                    allAppDatas.add(
                        AppInfo(
                            i.loadIcon(packageManager),
                            i.loadLabel(packageManager),
                            i.packageName,
                        )
                    )
                }
            }

            binding.recyclerView.apply {
                darkModeAdapter = DarkModeAdapter(context, allAppDatas, enabledDarkMode)
                adapter = darkModeAdapter
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
        if (menuItem.itemId == 1) requireActivity().restartScopes(scopes)
        if (menuItem.itemId == 2) jumpDarkMode(requireActivity())
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
class DarkModeAdapter(
    val context: Context,
    allAppInfos: ArrayList<AppInfo>,
    allEnableData: ArrayList<DarkModeInfo>
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

        allDatas = allAppInfos.apply {
            sortBy { it.appName.toString() }
        }

        val sortDatas = ArrayList<AppInfo>()
        allEnableData.forEach { its ->
            val find = allDatas.find { it.packName == its.packName }
            if (find != null) {
                enabledAppData[its.packName] = its
                sortDatas.add(find)
            }
        }
        saveEnableList()

        allDatas.apply {
            removeIf { sortDatas.contains(it) }
            addAll(0, sortDatas.apply {
                sortBy { it.appName.toString() }
            })
        }

        filterDatas = allDatas
        refreshDatas()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutAppinfoSwitchItemDarkmodeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appIcon = filterDatas[position].appIcon
        val appName = filterDatas[position].appName
        val packName = filterDatas[position].packName
        val data = enabledAppData[packName]

        holder.appIcon.setImageDrawable(appIcon)
        holder.appName.text = appName
        holder.packName.text = packName
        holder.appInfoView.setOnClickListener(null)
        holder.switchview.setOnCheckedChangeListener(null)
        holder.sliderview.clearOnChangeListeners()

        holder.switchview.isChecked = data != null
        holder.sliderLayout.isVisible = holder.switchview.isChecked
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
            filterDatas = if (constraint.isBlank()) {
                allDatas
            } else {
                val filterlist = ArrayList<AppInfo>()
                allDatas.forEach {
                    if (it.appName.toString().lowercase().contains(
                            constraint.toString().lowercase()
                        ) || it.packName.lowercase().contains(constraint.toString().lowercase())
                    ) {
                        filterlist.add(it)
                    }
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