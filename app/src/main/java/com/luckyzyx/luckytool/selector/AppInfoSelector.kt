package com.luckyzyx.luckytool.selector

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.utils.scope
import com.drake.net.utils.withDefault
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.databinding.DialogAppInfoSelectorLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoCheckboxItemBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoItemBinding
import com.luckyzyx.luckytool.listener.OnSelectAppInfoListener
import com.luckyzyx.luckytool.listener.OnSortChipListener
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.dialogCentered
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@Suppress("unused")
@Obfuscate
class AppInfoSelector(private val context: Context, private val multiChoice: Boolean) {

    private val binding = DialogAppInfoSelectorLayoutBinding.inflate(LayoutInflater.from(context))
    private var singleSelectorAdapter: AppInfoSingleSelectorAdapter? = null
    private var multiSelectorAdapter: AppInfoMultiSelectorAdapter? = null
    private val dialogBuilder = MaterialAlertDialogBuilder(context, dialogCentered).apply {
//        setCancelable(false)
        setView(binding.root)
    }
    private lateinit var dialog: AlertDialog

    private var allAppInfos = ArrayList<AppInfo>()
    private var enabledList = ArrayList<String>()

    private var enableSortFilter = true
    private var isReverse = false
    private var sortMode = 0
    private var showSystemApp = false

    private lateinit var sortFilterSelector: SortFilterSelector

    private var onSelectAppInfoListener: OnSelectAppInfoListener? = null

    init {
        initSortFilterSelector()
        initSearchViewLayout()
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                singleSelectorAdapter?.getFilter?.filter(text)
                multiSelectorAdapter?.getFilter?.filter(text)
            })
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
        binding.btnOk.apply {
            isVisible = multiChoice
            setOnClickListener {
                dialog.dismiss()
                val infos = multiSelectorAdapter?.getEnabledInfos() ?: arrayListOf()
                onSelectAppInfoListener?.resultSelectAppInfos(infos)
            }
        }
    }

    fun show() {
        if (allAppInfos.isEmpty()) loadData()

        dialog = dialogBuilder.show()
    }

    fun setOnSelectAppListener(onSelectAppInfoListener: OnSelectAppInfoListener) {
        this.onSelectAppInfoListener = onSelectAppInfoListener
    }

    fun setDefaultShowSystem(show: Boolean) {
        this.showSystemApp = show
        initSortFilterSelector()
    }

    fun setEnableSortFilter(enable: Boolean) {
        this.enableSortFilter = enable
        initSearchViewLayout()
    }

    fun setEnabledList(list: ArrayList<String>) {
        enabledList = list
    }

    private fun initSortFilterSelector() {
        sortFilterSelector = SortFilterSelector(context).apply {
            setSortChips(true, context.resources.getStringArray(R.array.sort_selector_chips))
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
            setOnSortChipListener(object : OnSortChipListener {
                override fun onReverseChange(isReverse: Boolean) {
                    this@AppInfoSelector.isReverse = isReverse
                    loadData()
                }

                override fun onSortModeChange(sortMode: Int) {
                    this@AppInfoSelector.sortMode = sortMode
                    loadData()
                }
            })
        }
    }

    private fun initSearchViewLayout() {
        binding.searchViewLayout.apply {
            hint = "Name / PackageName"
            if (enableSortFilter) {
                endIconMode = TextInputLayout.END_ICON_CUSTOM
                setEndIconDrawable(R.drawable.baseline_filter_list_24)
                setEndIconOnClickListener {
                    sortFilterSelector.show()
                }
            }
        }
    }

    private fun loadData() {
        scope {
            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null
            allAppInfos.clear()

            val allEnableInfos = ArrayList<AppInfo>()
            withDefault {
                val packageManager = context.packageManager
                allAppInfos = PackageUtils(packageManager).getInstalledAppInfos(0, showSystemApp)
                enabledList.forEach { its ->
                    val find = allAppInfos.find { it.packageName == its }
                    if (find != null) allEnableInfos.add(find)
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
                allEnableInfos.apply {
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
                singleSelectorAdapter =
                    AppInfoSingleSelectorAdapter(dialog, allAppInfos, onSelectAppInfoListener)
                multiSelectorAdapter =
                    AppInfoMultiSelectorAdapter(dialog, allAppInfos, allEnableInfos)
                adapter = if (multiChoice.not()) singleSelectorAdapter
                else multiSelectorAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }

            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    @Obfuscate
    class AppInfoSingleSelectorAdapter(
        private val dialog: AlertDialog?,
        allAppInfos: ArrayList<AppInfo>,
        private val onSelectAppInfoListener: OnSelectAppInfoListener?
    ) : RecyclerView.Adapter<SingleViewHolder>() {
        val context = dialog?.context

        private var allDatas = ArrayList<AppInfo>()
        private var filterDatas = ArrayList<AppInfo>()

        init {
            allDatas.clear()
            filterDatas.clear()

            allDatas = allAppInfos
            filterDatas = allDatas
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
            val binding = LayoutAppinfoItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SingleViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return filterDatas.size
        }

        override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
            val appInfo = filterDatas[position]
            val appIcon = appInfo.icon
            val appName = appInfo.name
            val packName = appInfo.packageName

            holder.appIcon.setImageDrawable(appIcon)
            holder.appName.text = appName
            holder.packName.text = packName
            holder.appInfoView.setOnClickListener(null)

            holder.appInfoView.setOnClickListener {
                dialog?.dismiss()
                onSelectAppInfoListener?.resultSelectAppInfos(arrayListOf(appInfo))
            }
        }

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                filterDatas = if (constraint.isBlank()) allDatas
                else {
                    val filterlist = ArrayList<AppInfo>()
                    allDatas.forEach {
                        if (it.name.lowercase().contains(
                                constraint.toString().lowercase()
                            ) || it.packageName.lowercase()
                                .contains(constraint.toString().lowercase())
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

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
        }
    }

    @Obfuscate
    class AppInfoMultiSelectorAdapter(
        dialog: AlertDialog?,
        allAppInfos: ArrayList<AppInfo>,
        allEnableInfos: ArrayList<AppInfo>
    ) : RecyclerView.Adapter<MultiViewHolder>() {
        val context = dialog?.context

        private var allDatas = ArrayList<AppInfo>()
        private var filterDatas = ArrayList<AppInfo>()

        private var enabledAppData = ArrayList<AppInfo>()

        init {
            allDatas.clear()
            filterDatas.clear()
            enabledAppData.clear()

            allDatas = allAppInfos
            filterDatas = allDatas

            allEnableInfos.forEach {
                enabledAppData.add(it)
                allDatas.remove(it)
            }
            allDatas.addAll(0, allEnableInfos)
        }

        fun getEnabledInfos(): ArrayList<AppInfo> {
            return enabledAppData
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewHolder {
            val binding = LayoutAppinfoCheckboxItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return MultiViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return filterDatas.size
        }

        override fun onBindViewHolder(holder: MultiViewHolder, position: Int) {
            val appInfo = filterDatas[position]
            val appIcon = appInfo.icon
            val appName = appInfo.name
            val packName = appInfo.packageName

            holder.appIcon.setImageDrawable(appIcon)
            holder.appName.text = appName
            holder.packName.text = packName
            holder.appInfoView.setOnClickListener(null)
            holder.checkbox.setOnCheckedChangeListener(null)

            holder.checkbox.isChecked = enabledAppData.contains(appInfo)
            holder.appInfoView.setOnClickListener {
                holder.checkbox.performClick()
            }
            holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                enabledAppData.remove(appInfo)
                if (isChecked) enabledAppData.add(appInfo)
            }
        }

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                filterDatas = if (constraint.isBlank()) allDatas
                else {
                    val filterlist = ArrayList<AppInfo>()
                    allDatas.forEach {
                        if (it.name.lowercase().contains(
                                constraint.toString().lowercase()
                            ) || it.packageName.lowercase()
                                .contains(constraint.toString().lowercase())
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

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
        }
    }

    @Obfuscate
    class SingleViewHolder(binding: LayoutAppinfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val appInfoView: ConstraintLayout = binding.root
        val appIcon: ImageView = binding.appIcon
        val appName: TextView = binding.appName
        val packName: TextView = binding.packName
    }

    @Obfuscate
    class MultiViewHolder(binding: LayoutAppinfoCheckboxItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val appInfoView: ConstraintLayout = binding.root
        val appIcon: ImageView = binding.appIcon
        val appName: TextView = binding.appName
        val packName: TextView = binding.packName
        val checkbox: MaterialCheckBox = binding.checkboxView
    }
}