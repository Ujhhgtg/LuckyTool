package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
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
import com.google.android.material.materialswitch.MaterialSwitch
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.FragmentMutliAppApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoSwitchItemBinding
import com.luckyzyx.luckytool.utils.AppInfo
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.jumpMultiApp
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.setupMenuProvider

@Obfuscate
class MultiAppFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentMutliAppApplistLayoutBinding
    private var multiAppAdapter: MultiAppAdapter? = null

    private var allAppInfos = ArrayList<AppInfo>()

    private var isShowSystemApp = false

    private val showSystemAppKey = "show_system_app_multi_app"
    private val supportListKey = "multi_app_custom_list"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        isShowSystemApp = requireActivity().getBoolean(ModulePrefs, showSystemAppKey, false)
        binding = FragmentMutliAppApplistLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchViewLayout.apply {
            hint = "Name / PackageName"
            isHintEnabled = true
            isHintAnimationEnabled = true
        }
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                multiAppAdapter?.getFilter?.filter(text)
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

            withDefault {
                val packageManager = requireActivity().packageManager
                val appinfos = PackageUtils(packageManager).getInstalledApplications(0)
                for (i in appinfos) {
                    if (i.flags and ApplicationInfo.FLAG_SYSTEM == 1 && !isShowSystemApp) continue
                    allAppInfos.add(
                        AppInfo(
                            i.loadIcon(packageManager),
                            i.loadLabel(packageManager),
                            i.packageName,
                        )
                    )
                }
            }

            binding.recyclerView.apply {
                multiAppAdapter = MultiAppAdapter(context, allAppInfos, enableData)
                adapter = multiAppAdapter
                layoutManager = LinearLayoutManager(context)
            }
            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.app_list_menu, menu)
        menu.findItem(R.id.show_system_app).isChecked = isShowSystemApp
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
class MultiAppAdapter(
    val context: Context, allAppInfos: ArrayList<AppInfo>, allEnableData: Set<String>?
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

        allDatas = allAppInfos.apply {
            sortBy { it.appName.toString() }
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

        filterDatas = allAppInfos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutAppinfoSwitchItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appIcon = filterDatas[position].appIcon
        val appName = filterDatas[position].appName
        val packName = filterDatas[position].packName

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
                    if (it.appName.toString().lowercase().contains(
                            constraint.toString().lowercase()
                        ) || it.packName.lowercase().contains(constraint.toString().lowercase())
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