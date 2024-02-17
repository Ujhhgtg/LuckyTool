package com.luckyzyx.luckytool.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.utils.scope
import com.drake.net.utils.withDefault
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.databinding.DialogAppinfoSortFilterSheetBinding
import com.luckyzyx.luckytool.databinding.DialogApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutAppinfoCheckboxItemBinding
import com.luckyzyx.luckytool.listener.OnResultSelectAppListener
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.getStringSet
import me.zhanghai.android.fastscroll.FastScrollerBuilder

@Obfuscate
class SelectAppDialogUtils(val context: Context, val key: String) {

    private val binding = DialogApplistLayoutBinding.inflate(LayoutInflater.from(context))
    private val dialogBuilder = MaterialAlertDialogBuilder(context, dialogCentered).apply {
//        setCancelable(false)
        setView(binding.root)
    }
    private var selectAppAdapter: SelectAppDialogAdapter? = null
    private var dialog: AlertDialog? = null

    private var allAppInfos = ArrayList<AppInfo>()

    private lateinit var sortFilterBinding: DialogAppinfoSortFilterSheetBinding
    private lateinit var sortFilterBottomSheet: BottomSheetDialog
    private var isReverse = false
    private var sortMode = 0
    private var showSystemApp = false

    private var onResultSelectAppListener: OnResultSelectAppListener? = null

    init {
        initSortFilterBottomSheet()
        binding.searchViewLayout.apply {
            hint = "Name / PackageName"
            setEndIconOnClickListener {
                sortFilterBottomSheet.show()
            }
        }
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                selectAppAdapter?.getFilter?.filter(text)
            })
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener { loadData() }
        }
        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        binding.btnOk.setOnClickListener {
            dialog?.dismiss()
            val infos = selectAppAdapter?.getEnabledInfos() ?: arrayListOf()
            onResultSelectAppListener?.resultSelectAppInfos(infos)
        }
    }

    fun show() {
        if (allAppInfos.isEmpty()) loadData()

        dialog = dialogBuilder.show()
    }

    fun setOnResultSelectAppListener(onResultSelectAppListener: OnResultSelectAppListener) {
        this.onResultSelectAppListener = onResultSelectAppListener
    }

    fun setDefaultShowSystem(show: Boolean) {
        this.showSystemApp = show
    }

    private fun loadData() {
        scope {
            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null
            allAppInfos.clear()

            val enableData = if (key.isNotBlank()) {
                context.getStringSet(ModulePrefs, key, ArraySet())?.toMutableList()
                    ?: mutableListOf()
            } else mutableListOf()

            val allEnableInfos = ArrayList<AppInfo>()
            withDefault {
                val packageManager = context.packageManager
                allAppInfos =
                    PackageUtils(packageManager).getInstalledAppInfos(0, showSystemApp)
                enableData.forEach { its ->
                    val find = allAppInfos.find { it.packName == its }
                    if (find != null) allEnableInfos.add(find)
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
                allEnableInfos.apply {
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
                selectAppAdapter = SelectAppDialogAdapter(context, allAppInfos, allEnableInfos)
                adapter = selectAppAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }
            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    private fun initSortFilterBottomSheet() {
        sortFilterBinding =
            DialogAppinfoSortFilterSheetBinding.inflate(LayoutInflater.from(context))
        sortFilterBottomSheet = BottomSheetDialog(context).apply {
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
                context.getString(R.string.appinfo_app_name),
                context.getString(R.string.appinfo_package_name),
                context.getString(R.string.appinfo_app_size),
                context.getString(R.string.appinfo_install_time),
                context.getString(R.string.appinfo_last_updated_time),
                context.getString(R.string.appinfo_target_sdk)
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
            arrayOf(context.getString(R.string.appinfo_system_app)).forEachIndexed { index, title ->
                addView(getFilterChip(index, title))
            }
        }
    }

    private fun getSortChip(index: Int, title: String): Chip {
        return Chip(context).apply {
            text = title
            isCheckable = true
            isClickable = true
            isChecked = index == 0
        }
    }

    private fun getFilterChip(index: Int, title: String): Chip {
        return Chip(context).apply {
            text = title
            isCheckable = true
            isClickable = true
            when (index) {
                0 -> {
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                        showSystemApp = isChecked
                        loadData()
                    }
                }
            }
        }
    }

    class SelectAppDialogAdapter(
        val context: Context, allAppInfos: ArrayList<AppInfo>, allEnableInfos: ArrayList<AppInfo>
    ) : RecyclerView.Adapter<SelectAppDialogAdapter.ViewHolder>() {

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = LayoutAppinfoCheckboxItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return filterDatas.size
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
                        if (it.name.lowercase().contains(constraint.toString().lowercase())
                            || it.packName.lowercase()
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

        class ViewHolder(binding: LayoutAppinfoCheckboxItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val appInfoView: ConstraintLayout = binding.root
            val appIcon: ImageView = binding.appIcon
            val appName: TextView = binding.appName
            val packName: TextView = binding.packName
            val checkbox: MaterialCheckBox = binding.checkboxView
        }
    }
}