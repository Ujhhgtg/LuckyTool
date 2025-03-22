package com.luckyzyx.luckytool.selector

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ResolveInfo
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.luckyzyx.luckytool.databinding.DialogActivityInfoSelectorLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutActivityinfoCheckboxItemBinding
import com.luckyzyx.luckytool.databinding.LayoutActivityinfoItemBinding
import com.luckyzyx.luckytool.listener.OnSelectResolveInfoListener
import com.luckyzyx.luckytool.utils.dialogCentered
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import org.lsposed.lsparanoid.Obfuscate

@Suppress("unused")
@Obfuscate
class ResolveInfoSelector(
    val context: Context, private val multiMode: Boolean, private val resolves: Array<ResolveInfo>?
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

    private var allResolveInfos = ArrayList<ResolveInfo>()
    private var allEnabledInfos = ArrayList<ResolveInfo>()
    private var enabledList = ArrayList<String>()

    private var onSelectResolveInfoListener: OnSelectResolveInfoListener? = null

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
                onSelectResolveInfoListener?.resultSelectResolveInfos(infos)
            }
        }
    }

    fun show() {
        if (allResolveInfos.isEmpty()) loadData()

        dialog = dialogBuilder.show()
    }

    fun setOnSelectResolveInfoListener(onSelectResolveInfoListener: OnSelectResolveInfoListener) {
        this.onSelectResolveInfoListener = onSelectResolveInfoListener
    }

    fun setEnabledList(list: ArrayList<String>) {
        enabledList = list
    }

    private fun loadData() {
        scope {
            allResolveInfos.clear()
            allEnabledInfos.clear()

            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null

            withDefault {
                allResolveInfos = ArrayList(resolves?.toList() ?: arrayListOf())
                allResolveInfos.forEach {
                    if (enabledList.contains(it.activityInfo.name)) {
                        allEnabledInfos.add(it)
                    }
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

    @Obfuscate
    inner class ActivityInfoSingleSelectorAdapter : RecyclerView.Adapter<SingleViewHolder>() {

        private var filterDatas = ArrayList<ResolveInfo>()

        init {
            filterDatas.clear()
            filterDatas = allResolveInfos
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
            val binding = LayoutActivityinfoItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SingleViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return filterDatas.size
        }

        override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
            val resolveInfo = filterDatas[position]
            val appIcon = resolveInfo.loadIcon(context.packageManager)
            val label = resolveInfo.loadLabel(context.packageManager)
            val name = resolveInfo.activityInfo.name

            holder.activityIcon.setImageDrawable(appIcon)
            holder.activityLabel.text = label
            holder.activityName.text = name
            holder.activityInfoView.setOnClickListener(null)

            holder.activityInfoView.setOnClickListener {
                dialog.dismiss()
                onSelectResolveInfoListener?.resultSelectResolveInfos(arrayListOf(resolveInfo))
            }
        }

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterStr = constraint.toString().lowercase()
                filterDatas = if (constraint.isBlank()) allResolveInfos
                else {
                    val filterlist = ArrayList<ResolveInfo>()
                    allResolveInfos.forEach {
                        val label = it.loadLabel(context.packageManager)
                        if (it.activityInfo.name.lowercase().contains(filterStr)
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
                filterDatas = results.values as ArrayList<ResolveInfo>
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

        private var filterDatas = ArrayList<ResolveInfo>()
        private var enabledDatas = ArrayList<ResolveInfo>()

        init {
            filterDatas.clear()
            filterDatas = allResolveInfos

            filterDatas = allResolveInfos

            allEnabledInfos.forEach {
                enabledDatas.add(it)
                filterDatas.remove(it)
            }

            filterDatas.addAll(0, enabledDatas)
        }

        fun getEnabledInfos(): ArrayList<ResolveInfo> {
            return enabledDatas
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewHolder {
            val binding = LayoutActivityinfoCheckboxItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return MultiViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return filterDatas.size
        }

        override fun onBindViewHolder(holder: MultiViewHolder, position: Int) {
            val resolveInfo = filterDatas[position]
            val appIcon = resolveInfo.loadIcon(context.packageManager)
            val label = resolveInfo.loadLabel(context.packageManager)
            val name = resolveInfo.activityInfo.name

            holder.activityIcon.setImageDrawable(appIcon)
            holder.activityLabel.text = label
            holder.activityName.text = name
            holder.activityInfoView.setOnClickListener(null)
            holder.checkbox.setOnCheckedChangeListener(null)

            holder.checkbox.isChecked = enabledDatas.contains(resolveInfo)
            holder.activityInfoView.setOnClickListener {
                holder.checkbox.performClick()
            }
            holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                enabledDatas.remove(resolveInfo)
                if (isChecked) enabledDatas.add(resolveInfo)
            }
        }

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterStr = constraint.toString().lowercase()
                filterDatas = if (constraint.isBlank()) allResolveInfos
                else {
                    val filterlist = ArrayList<ResolveInfo>()
                    allResolveInfos.forEach {
                        val label = it.loadLabel(context.packageManager)
                        if (it.activityInfo.name.lowercase().contains(filterStr)
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
                filterDatas = results.values as ArrayList<ResolveInfo>
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