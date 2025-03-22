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
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppIntentInfo
import com.luckyzyx.luckytool.databinding.DialogActivityInfoSelectorLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutActivityinfoCheckboxItemBinding
import com.luckyzyx.luckytool.databinding.LayoutActivityinfoItemBinding
import com.luckyzyx.luckytool.listener.OnSelectIntentInfoListener
import com.luckyzyx.luckytool.utils.dialogCentered
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import org.lsposed.lsparanoid.Obfuscate

@Suppress("unused")
@Obfuscate
class IntentInfoSelector(
    val context: Context, private val multiMode: Boolean,
    private val resolves: ArrayList<AppIntentInfo>
) {

    private val binding =
        DialogActivityInfoSelectorLayoutBinding.inflate(LayoutInflater.from(context))
    private var singleSelectorAdapter: ActivityInfoSingleSelectorAdapter? = null
    private var multiSelectorAdapter: ActivityInfoMultiSelectorAdapter? = null
    private val dialogBuilder = MaterialAlertDialogBuilder(context, dialogCentered).apply {
//        setCancelable(false)
        setView(binding.root)
    }
    private lateinit var dialog: AlertDialog

    private var allIntentInfos = ArrayList<AppIntentInfo>()
    private var allEnabledInfos = ArrayList<AppIntentInfo>()
    private var enabledList = ArrayList<AppIntentInfo>()

    private var onSelectIntentInfoListener: OnSelectIntentInfoListener? = null
    private var selectAllMode = false
    private var showAppIcon = true

    init {
        binding.searchViewLayout.apply {
            hint = "ActivityName"
            endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
        }
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
            isVisible = multiMode
            setOnClickListener {
                dialog.dismiss()
                val infos = multiSelectorAdapter?.getEnabledInfos() ?: arrayListOf()
                onSelectIntentInfoListener?.resultSelectIntentInfos(infos)
            }
        }
    }

    fun show() {
        if (allIntentInfos.isEmpty()) loadData()

        dialog = dialogBuilder.show()
    }

    fun setOnSelectIntentInfoListener(onSelectIntentInfoListener: OnSelectIntentInfoListener) {
        this.onSelectIntentInfoListener = onSelectIntentInfoListener
    }

    fun setEnabledList(list: ArrayList<AppIntentInfo>) {
        enabledList = list
    }

    fun setShowIcon(mode: Boolean) {
        showAppIcon = mode
    }

    fun setSelectAllMode(mode: Boolean) {
        selectAllMode = mode

        binding.btnSelectAll.apply {
            isVisible = multiMode && selectAllMode
            setOnClickListener {
                val isAll = allIntentInfos.size == multiSelectorAdapter?.getEnabledInfos()?.size
                multiSelectorAdapter?.setEnabledInfos(
                    if (isAll) arrayListOf() else allIntentInfos
                )
                multiSelectorAdapter?.refreshDatas()
            }
        }
    }

    private fun loadData() {
        scope {
            allIntentInfos.clear()
            allEnabledInfos.clear()

            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null

            allIntentInfos.addAll(resolves)
            allIntentInfos.forEach {
                if (enabledList.contains(it)) {
                    allEnabledInfos.add(it)
                }
            }

            binding.recyclerView.apply {
                singleSelectorAdapter = ActivityInfoSingleSelectorAdapter()
                multiSelectorAdapter = ActivityInfoMultiSelectorAdapter()
                adapter = if (multiMode.not()) singleSelectorAdapter
                else multiSelectorAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }

            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    private fun formatType(type: String): String {
        return when (type) {
            "single_share" -> context.getString(R.string.intent_single_share)
            "multi_share" -> context.getString(R.string.intent_multi_share)
            "process_text" -> context.getString(R.string.intent_long_press_text)
            "content_view" -> context.getString(R.string.intent_open_content)
            "http_link" -> context.getString(R.string.intent_http_link)
            "https_link" -> context.getString(R.string.intent_https_link)
            else -> type
        }
    }

    @Obfuscate
    inner class ActivityInfoSingleSelectorAdapter : RecyclerView.Adapter<SingleViewHolder>() {

        private var filterDatas = ArrayList<AppIntentInfo>()

        init {
            filterDatas.clear()
            filterDatas = allIntentInfos
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
            val binding = LayoutActivityinfoItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            binding.activityIcon.isVisible = showAppIcon
            return SingleViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return filterDatas.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
            val info = filterDatas[position]
            val appIcon = info.resolveInfo.loadIcon(context.packageManager)
            val label = info.resolveInfo.loadLabel(context.packageManager)
            val name = info.resolveInfo.activityInfo.name
            val type = info.type

            holder.activityIcon.setImageDrawable(appIcon)
            holder.activityLabel.text = "$label $type"
            holder.activityName.text = name
            holder.activityInfoView.setOnClickListener(null)

            holder.activityInfoView.setOnClickListener {
                dialog.dismiss()
                onSelectIntentInfoListener?.resultSelectIntentInfos(arrayListOf(info))
            }
        }

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterStr = constraint.toString().lowercase()
                filterDatas = if (constraint.isBlank()) allIntentInfos
                else {
                    val filterlist = ArrayList<AppIntentInfo>()
                    allIntentInfos.forEach {
                        val label = it.resolveInfo.loadLabel(context.packageManager)
                        val activity = it.resolveInfo.activityInfo.name
                        if (activity.lowercase().contains(filterStr)
                            || label.toString().lowercase().contains(filterStr)
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
                filterDatas = results.values as ArrayList<AppIntentInfo>
                refreshDatas()
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
        }
    }

    @Obfuscate
    inner class ActivityInfoMultiSelectorAdapter : RecyclerView.Adapter<MultiViewHolder>() {

        private var filterDatas = ArrayList<AppIntentInfo>()
        private var enabledDatas = ArrayList<AppIntentInfo>()

        init {
            filterDatas.clear()
            filterDatas = allIntentInfos

            allEnabledInfos.forEach {
                enabledDatas.add(it)
                filterDatas.remove(it)
            }

            filterDatas.addAll(0, enabledDatas)
        }

        fun setEnabledInfos(infos: ArrayList<AppIntentInfo>) {
            enabledDatas = infos
        }

        fun getEnabledInfos(): ArrayList<AppIntentInfo> {
            return enabledDatas
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewHolder {
            val binding = LayoutActivityinfoCheckboxItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            binding.activityIcon.isVisible = showAppIcon
            return MultiViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return filterDatas.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MultiViewHolder, position: Int) {
            val info = filterDatas[position]
            val appIcon = info.resolveInfo.loadIcon(context.packageManager)
            val label = info.resolveInfo.loadLabel(context.packageManager)
            val name = info.resolveInfo.activityInfo.name
            val type = info.type

            holder.activityIcon.setImageDrawable(appIcon)
            holder.activityLabel.text = "$label ${formatType(type)}"
            holder.activityName.text = name
            holder.activityInfoView.setOnClickListener(null)
            holder.checkbox.setOnCheckedChangeListener(null)

            holder.checkbox.isChecked = enabledDatas.contains(info)
            holder.activityInfoView.setOnClickListener {
                holder.checkbox.performClick()
            }
            holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                enabledDatas.remove(info)
                if (isChecked) enabledDatas.add(info)
            }
        }

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterStr = constraint.toString().lowercase()
                filterDatas = if (constraint.isBlank()) allIntentInfos
                else {
                    val filterlist = ArrayList<AppIntentInfo>()
                    allIntentInfos.forEach {
                        val label = it.resolveInfo.loadLabel(context.packageManager)
                        val activity = it.resolveInfo.activityInfo.name
                        if (activity.lowercase().contains(filterStr)
                            || label.toString().lowercase().contains(filterStr)
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
                filterDatas = results.values as ArrayList<AppIntentInfo>
                refreshDatas()
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
        }
    }

    @Obfuscate
    class SingleViewHolder(binding: LayoutActivityinfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val activityInfoView: ConstraintLayout = binding.root
        val activityIcon: ImageView = binding.activityIcon
        val activityLabel: TextView = binding.activityLabel
        val activityName: TextView = binding.activityName
    }

    @Obfuscate
    class MultiViewHolder(binding: LayoutActivityinfoCheckboxItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val activityInfoView: ConstraintLayout = binding.root
        val activityIcon: ImageView = binding.activityIcon
        val activityLabel: TextView = binding.activityLabel
        val activityName: TextView = binding.activityName
        val checkbox: MaterialCheckBox = binding.checkboxView
    }
}