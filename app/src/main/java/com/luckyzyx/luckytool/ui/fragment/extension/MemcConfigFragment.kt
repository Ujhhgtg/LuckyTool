package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.drake.net.utils.scopeLife
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import org.lsposed.lsparanoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.data.MemcConfigActivity
import com.luckyzyx.luckytool.data.MemcConfigPackage
import com.luckyzyx.luckytool.databinding.DialogMemcConfigLayoutBinding
import com.luckyzyx.luckytool.databinding.FragmentMemcActivityLayoutBinding
import com.luckyzyx.luckytool.databinding.FragmentMemcLayoutBinding
import com.luckyzyx.luckytool.databinding.FragmentMemcPackageLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutMemcActivityItemBinding
import com.luckyzyx.luckytool.databinding.LayoutMemcPackageItemBinding
import com.luckyzyx.luckytool.listener.OnSelectActivityInfoListener
import com.luckyzyx.luckytool.listener.OnSelectAppInfoListener
import com.luckyzyx.luckytool.selector.ActivityInfoSelector
import com.luckyzyx.luckytool.selector.AppInfoSelector
import com.luckyzyx.luckytool.utils.CommandUtils
import com.luckyzyx.luckytool.utils.FileUtils
import com.luckyzyx.luckytool.utils.GlobalKeyValue
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.safeOfNull
import com.luckyzyx.luckytool.utils.setupMenuProvider
import com.luckyzyx.luckytool.utils.showToast
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.io.InputStream

@Obfuscate
class MemcConfigFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentMemcLayoutBinding
    private var memcPagerAdapter: MemcPagerAdapter? = null

    private lateinit var memcPackageFragment: MemcPackageFragment
    private lateinit var memcActivityFragment: MemcActivityFragment

    private val configPackageList = GlobalKeyValue.memcConfigPackageList
    private val configActivityList = GlobalKeyValue.memcConfigActivityList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        binding = FragmentMemcLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadData()
    }

    private fun loadData() {
        scopeLife {
            val configPackages =
                requireActivity().getStringSet(ModulePrefs, configPackageList, ArraySet())
            val configActivitys =
                requireActivity().getStringSet(ModulePrefs, configActivityList, ArraySet())

            if (configPackages.isEmpty() || configActivitys.isEmpty()) {
                resetAllConfig()
            }

            memcPackageFragment = MemcPackageFragment()
            memcActivityFragment = MemcActivityFragment()

            binding.viewPager.apply {
                memcPagerAdapter = MemcPagerAdapter(
                    childFragmentManager, lifecycle,
                    arrayListOf(memcPackageFragment, memcActivityFragment)
                )
                adapter = memcPagerAdapter
                offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            }
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Packages"
                    1 -> "Activitys"
                    else -> null
                }
            }.attach()
        }
    }

    private val restoreData = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            val inputStream = safeOfNull {
                requireActivity().contentResolver.openInputStream(it)
            } ?: return@registerForActivityResult
            resetAllConfig(inputStream)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.common_words_import) + "Xml").apply {
//            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 2, 0, getString(R.string.common_words_reset)).apply {
//            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            1 -> {
                FileUtils.checkDownloadDir(requireActivity(), "LuckyTool").apply {
                    if (isFile) delete()
                    if (!exists()) mkdirs()
                }
                restoreData.launch("text/xml")
            }

            2 -> {
                val version = arrayOf("x7", "x7p")
                MaterialAlertDialogBuilder(requireActivity(), dialogCentered).apply {
                    setMessage(getString(R.string.restore_frame_insertion_configuration_data))
                    setPositiveButton(android.R.string.ok) { _, _ ->
                        MaterialAlertDialogBuilder(context, dialogCentered).apply {
                            setItems(version) { _, which ->
                                resetAllConfig(null, version[which])
                            }
                        }.show()
                    }
                    setNeutralButton(android.R.string.cancel, null)
                }.show()
            }
        }
        return true
    }

    private fun resetAllConfig(inputStream: InputStream? = null, version: String = "") {
        scopeLife {
            val packages = java.util.ArrayList<MemcConfigPackage>()
            val activitys = java.util.ArrayList<MemcConfigActivity>()

            val newInputStream = inputStream ?: safeOfNull {
                requireActivity().resources.openRawResource(R.raw.multimedia_pixelworks_apps_x7)
            } ?: return@scopeLife
            FileUtils.parseMemcXml(newInputStream, packages, activitys)

            when (version) {
                "x7p" -> activitys.onEachIndexed { index, config ->
                    activitys[index] =
                        MemcConfigActivity(config.packName, config.activity, "258-10-0-0")
                }
            }

            val packageSet = ArraySet<String>()
            val activitySet = ArraySet<String>()

            if (packages.isNotEmpty() && activitys.isNotEmpty()) {
                MemcCallback.callback?.invoke(configPackageList, packages)
                MemcCallback.callback?.invoke(configActivityList, activitys)
            }

            packages.forEachIndexed { _, info ->
                packageSet.add(info.toJSONObject().toString())
            }
            activitys.forEachIndexed { _, info ->
                activitySet.add(info.toJSONObject().toString())
            }
            if (packageSet.isNotEmpty() && activitySet.isNotEmpty()) {
                requireActivity().putStringSet(ModulePrefs, configPackageList, packageSet)
                requireActivity().putStringSet(ModulePrefs, configActivityList, activitySet)
            }
        }
    }

    @Obfuscate
    object MemcCallback {
        var callback: ((key: String, value: Any) -> Unit)? = null
    }

    @Obfuscate
    class MemcPackageFragment : Fragment() {
        private lateinit var binding: FragmentMemcPackageLayoutBinding
        private var memcPackageAdapter: MemcPackageAdapter? = null

        private val allConfigPackages = ArrayList<MemcConfigPackage>()

        private val configPackageList = GlobalKeyValue.memcConfigPackageList
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View {
            binding = FragmentMemcPackageLayoutBinding.inflate(inflater)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            scopeLife {
                binding.searchViewLayout.apply {
                    hint = "PackageName"
                }
                binding.searchView.apply {
                    addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                        memcPackageAdapter?.getFilter?.filter(text)
                    })
                }
                binding.swipeRefreshLayout.apply {
                    setOnRefreshListener { loadData() }
                }
                binding.addData.apply {
                    setOnClickListener {
                        memcPackageAdapter?.addOrEditData()
                    }
                }

                MemcCallback.callback = { key: String, value: Any ->
                    if (key == configPackageList) loadData(value)
                }

                loadData()
            }
        }

        private fun loadData(value: Any? = null) {
            scopeLife {
                binding.swipeRefreshLayout.isRefreshing = true
                binding.searchViewLayout.isEnabled = false
                binding.searchView.text = null

                allConfigPackages.clear()

                if (value == null) {
                    val configPackages =
                        requireActivity().getStringSet(ModulePrefs, configPackageList, ArraySet())
                    configPackages.forEach {
                        val configPackageInfo = MemcConfigPackage().toMemcConfigPackage(it)
                        if (configPackageInfo != null) allConfigPackages.add(configPackageInfo)
                    }
                } else {
                    @Suppress("UNCHECKED_CAST")
                    allConfigPackages.addAll(value as java.util.ArrayList<MemcConfigPackage>)
                }

                binding.noMemcData.apply {
                    isVisible = allConfigPackages.isEmpty()
                }

                binding.recyclerView.apply {
                    memcPackageAdapter = MemcPackageAdapter(context, allConfigPackages)
                    adapter = memcPackageAdapter
                    layoutManager = LinearLayoutManager(context)
                    FastScrollerBuilder(this).useMd2Style().build()
                }

                binding.searchViewLayout.isEnabled = true
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        class MemcPackageAdapter(
            val context: Context,
            allConfigPackages: ArrayList<MemcConfigPackage>
        ) : RecyclerView.Adapter<MemcPackageAdapter.ViewHolder>() {
            private val configPackageList = GlobalKeyValue.memcConfigPackageList

            var allDatas = java.util.ArrayList<MemcConfigPackage>()
            var filterDatas = java.util.ArrayList<MemcConfigPackage>()

            init {
                allDatas = allConfigPackages.apply {
                    sortBy { it.packName }
                }
                filterDatas = allDatas
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val binding = LayoutMemcPackageItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ViewHolder(binding)
            }

            override fun getItemCount(): Int {
                return filterDatas.size
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val config = filterDatas[position]
                val packName = config.packName
                val rate = config.rate
                val type = config.type

                holder.card.setOnClickListener(null)
                holder.card.setOnLongClickListener(null)

                holder.card.setOnClickListener {
                    addOrEditData(config)
                }

                holder.packageName.apply {
                    text = packName
                }
                holder.screenRate.apply {
                    text = "Rate: $rate"
                }
                holder.commandType.apply {
                    text = "Type: $type"
                }
            }

            val getFilter = object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val filterStr = constraint.toString().lowercase()
                    filterDatas = if (constraint.isBlank()) {
                        allDatas
                    } else {
                        val filterlist = ArrayList<MemcConfigPackage>()
                        allDatas.forEach {
                            if (it.packName.lowercase().contains(filterStr)) filterlist.add(it)
                        }
                        filterlist
                    }
                    val filterResults = FilterResults()
                    filterResults.values = filterDatas
                    return filterResults
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence, results: FilterResults) {
                    filterDatas = results.values as ArrayList<MemcConfigPackage>
                    refreshDatas()
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            fun refreshDatas() {
                notifyDataSetChanged()
            }

            fun addOrEditData(config: MemcConfigPackage? = null) {
                val binding = DialogMemcConfigLayoutBinding.inflate(LayoutInflater.from(context))
                binding.packageLayout.hint = "PackageName"
                binding.activityLayout.isVisible = false
                binding.rateLayout.hint = "ScreenRate"
                binding.typeLayout.hint = "Type"

                if (config != null) {
                    binding.packageView.setText(config.packName)
                    binding.rateView.setText(config.rate)
                    binding.typeView.setText(config.type)
                }

                binding.packageView.apply {
                    setOnClickListener {
                        AppInfoSelector(context, false).apply {
                            setOnSelectAppListener(object : OnSelectAppInfoListener {
                                override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                    if (list.isEmpty()) return
                                    setText(list.first().packageName)
                                }
                            })
                            show()
                        }
                    }
                }

                binding.tipsView.text = context.getString(
                    R.string.edit_memc_configuration_tips, CommandUtils.memcHdrConfigHelp
                )

                MaterialAlertDialogBuilder(context, dialogCentered).apply {
                    setView(binding.root)
                    setPositiveButton(android.R.string.ok) { _, _ ->
                        val packageName = binding.packageView.text?.toString()
                        val rate = binding.rateView.text?.toString()
                        val type = binding.typeView.text?.toString()
                        if (!(packageName.isNullOrBlank() || rate.isNullOrBlank() || type.isNullOrBlank())) {
                            val newConfig = MemcConfigPackage(packageName, rate, type)
                            if (config != null) {
                                val index = allDatas.indexOf(config)
                                if (index != -1) allDatas[index] = newConfig
                                else allDatas.add(newConfig)
                            } else allDatas.add(newConfig)
                            saveAllData()
                        } else context.showToast("Data is incomplete!")
                    }
                    if (config != null) {
                        setNeutralButton(R.string.common_words_remove) { _, _ ->
                            MaterialAlertDialogBuilder(context, dialogCentered).apply {
                                val msg = context.getString(
                                    R.string.confirm_to_delete_this_configuration, config.packName
                                )
                                setMessage(msg)
                                setPositiveButton(android.R.string.ok) { _, _ ->
                                    allDatas.remove(config)
                                    saveAllData()
                                }
                                setNeutralButton(android.R.string.cancel, null)
                            }.show()
                        }
                    }
                    setNegativeButton(android.R.string.cancel, null)
                }.show()
            }

            private fun saveAllData() {
                val set = ArraySet<String>()
                allDatas.forEach {
                    set.add(it.toJSONObject().toString())
                }
                filterDatas = allDatas
                if (set.isNotEmpty()) {
                    context.putStringSet(ModulePrefs, configPackageList, set.toSet())
                }
                refreshDatas()
            }

            class ViewHolder(binding: LayoutMemcPackageItemBinding) :
                RecyclerView.ViewHolder(binding.root) {
                val card = binding.root
                val packageName = binding.packageName
                val screenRate = binding.screenRate
                val commandType = binding.commandType
            }
        }
    }

    @Obfuscate
    class MemcActivityFragment : Fragment() {
        private lateinit var binding: FragmentMemcActivityLayoutBinding
        private var memcActivityAdapter: MemcActivityAdapter? = null

        private val allConfigActivitys = ArrayList<MemcConfigActivity>()

        private val configActivityList = GlobalKeyValue.memcConfigActivityList
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View {
            binding = FragmentMemcActivityLayoutBinding.inflate(inflater)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            scopeLife {
                binding.searchViewLayout.apply {
                    hint = "PackageName / ActivityName"
                }
                binding.searchView.apply {
                    addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                        memcActivityAdapter?.getFilter?.filter(text)
                    })
                }
                binding.swipeRefreshLayout.apply {
                    setOnRefreshListener { loadData() }
                }
                binding.addData.apply {
                    setOnClickListener {
                        memcActivityAdapter?.addOrEditData()
                    }
                }

                MemcCallback.callback = { key: String, value: Any ->
                    if (key == configActivityList) loadData(value)
                }

                loadData()
            }
        }

        private fun loadData(value: Any? = null) {
            scopeLife {
                binding.swipeRefreshLayout.isRefreshing = true
                binding.searchViewLayout.isEnabled = false
                binding.searchView.text = null

                allConfigActivitys.clear()

                if (value == null) {
                    val configActivitys =
                        requireActivity().getStringSet(ModulePrefs, configActivityList, ArraySet())
                    configActivitys.forEach {
                        val configActivityInfo = MemcConfigActivity().toMemcConfigActivity(it)
                        if (configActivityInfo != null) allConfigActivitys.add(configActivityInfo)
                    }
                } else {
                    @Suppress("UNCHECKED_CAST")
                    allConfigActivitys.addAll(value as java.util.ArrayList<MemcConfigActivity>)
                }

                binding.noMemcData.apply {
                    isVisible = allConfigActivitys.isEmpty()
                }

                binding.recyclerView.apply {
                    memcActivityAdapter = MemcActivityAdapter(context, allConfigActivitys)
                    adapter = memcActivityAdapter
                    layoutManager = LinearLayoutManager(context)
                    FastScrollerBuilder(this).useMd2Style().build()
                }

                binding.searchViewLayout.isEnabled = true
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        class MemcActivityAdapter(
            val context: Context,
            allConfigActivitys: ArrayList<MemcConfigActivity>
        ) : RecyclerView.Adapter<MemcActivityAdapter.ViewHolder>() {
            private val configActivityList = GlobalKeyValue.memcConfigActivityList

            var allDatas = java.util.ArrayList<MemcConfigActivity>()
            var filterDatas = java.util.ArrayList<MemcConfigActivity>()

            init {
                allDatas = allConfigActivitys.apply {
                    sortBy { it.packName }
                }
                filterDatas = allDatas
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val binding = LayoutMemcActivityItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ViewHolder(binding)
            }

            override fun getItemCount(): Int {
                return filterDatas.size
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val config = filterDatas[position]
                val activity = config.activity
                val packName = config.packName
                val type = config.type

                holder.card.setOnClickListener(null)
                holder.card.setOnLongClickListener(null)

                holder.card.setOnClickListener {
                    addOrEditData(config)
                }

                holder.packageName.apply {
                    text = packName
                }
                holder.commandType.apply {
                    text = type
                }
                holder.activityName.apply {
                    text = activity
                }
            }

            val getFilter = object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    val filterStr = constraint.toString().lowercase()
                    filterDatas = if (constraint.isBlank()) {
                        allDatas
                    } else {
                        val filterlist = ArrayList<MemcConfigActivity>()
                        allDatas.forEach {
                            if (it.packName.lowercase().contains(filterStr)
                                || it.activity.lowercase().contains(filterStr)
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
                    filterDatas = results.values as ArrayList<MemcConfigActivity>
                    refreshDatas()
                }
            }

            fun addOrEditData(config: MemcConfigActivity? = null) {
                val binding = DialogMemcConfigLayoutBinding.inflate(LayoutInflater.from(context))
                binding.packageLayout.hint = "PackageName"
                binding.activityLayout.hint = "ActivityName"
                binding.rateLayout.isVisible = false
                binding.typeLayout.hint = "Type"

                if (config != null) {
                    binding.packageView.setText(config.packName)
                    binding.activityView.setText(config.activity)
                    binding.typeView.setText(config.type)
                }

                binding.packageView.apply {
                    setOnClickListener {
                        AppInfoSelector(context, false).apply {
                            setOnSelectAppListener(object : OnSelectAppInfoListener {
                                override fun resultSelectAppInfos(list: ArrayList<AppInfo>) {
                                    if (list.isEmpty()) return
                                    setText(list.first().packageName)
                                }
                            })
                            show()
                        }
                    }
                }

                binding.tipsView.text = context.getString(
                    R.string.edit_memc_configuration_tips, CommandUtils.memcConfigHelp
                )

                binding.activityView.apply {
                    setOnClickListener {
                        val packageName = binding.packageView.text?.toString()
                        val packInfo = packageName?.let {
                            PackageUtils(context.packageManager).getPackageInfo(
                                it, PackageManager.GET_ACTIVITIES
                            )
                        }
                        if (packageName.isNullOrBlank()) {
                            context.showToast("PackageName is null!")
                            return@setOnClickListener
                        }
                        if (packInfo == null) {
                            context.showToast("App data is null!")
                            return@setOnClickListener
                        }
                        ActivityInfoSelector(context, false, packInfo.activities).apply {
                            setOnSelectActivityListener(object : OnSelectActivityInfoListener {
                                override fun resultSelectActivityInfos(list: ArrayList<ActivityInfo>) {
                                    if (list.isEmpty()) return
                                    setText(list.first().name)
                                }
                            })
                            show()
                        }
                    }
                }

                MaterialAlertDialogBuilder(context, dialogCentered).apply {
                    setView(binding.root)
                    setPositiveButton(android.R.string.ok) { _, _ ->
                        val packageName = binding.packageView.text?.toString()
                        val activity = binding.activityView.text?.toString()
                        val type = binding.typeView.text?.toString()
                        if (!(packageName.isNullOrBlank() || activity.isNullOrBlank() || type.isNullOrBlank())) {
                            val newConfig = MemcConfigActivity(packageName, activity, type)
                            if (config != null) {
                                val index = allDatas.indexOf(config)
                                if (index != -1) allDatas[index] = newConfig
                            } else allDatas.add(newConfig)
                            saveAllData()
                        } else context.showToast("Data is incomplete!")
                    }
                    if (config != null) {
                        setNeutralButton(R.string.common_words_remove) { _, _ ->
                            MaterialAlertDialogBuilder(context, dialogCentered).apply {
                                val msg = context.getString(
                                    R.string.confirm_to_delete_this_configuration,
                                    config.activity
                                )
                                setMessage(msg)
                                setPositiveButton(android.R.string.ok) { _, _ ->
                                    allDatas.remove(config)
                                    saveAllData()
                                }
                                setNeutralButton(android.R.string.cancel, null)
                            }.show()
                        }
                    }
                    setNegativeButton(android.R.string.cancel, null)
                }.show()
            }

            @SuppressLint("NotifyDataSetChanged")
            fun refreshDatas() {
                notifyDataSetChanged()
            }

            private fun saveAllData() {
                val set = ArraySet<String>()
                allDatas.forEach {
                    set.add(it.toJSONObject().toString())
                }
                filterDatas = allDatas
                if (set.isNotEmpty()) {
                    context.putStringSet(ModulePrefs, configActivityList, set.toSet())
                }
                refreshDatas()
            }

            class ViewHolder(binding: LayoutMemcActivityItemBinding) :
                RecyclerView.ViewHolder(binding.root) {
                val card = binding.root
                val activityName = binding.activityName
                val packageName = binding.packageName
                val commandType = binding.commandType
            }
        }
    }

    @Obfuscate
    class MemcPagerAdapter(
        fragmentManager: FragmentManager, lifecycle: Lifecycle,
        private val fragmentList: List<Fragment>
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getItemCount(): Int {
            return fragmentList.size
        }
    }
}