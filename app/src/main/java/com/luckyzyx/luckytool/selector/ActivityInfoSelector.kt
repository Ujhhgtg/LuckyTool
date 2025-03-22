package com.luckyzyx.luckytool.selector

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
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
import com.luckyzyx.luckytool.listener.OnSelectActivityInfoListener
import com.luckyzyx.luckytool.utils.dialogCentered
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import org.lsposed.lsparanoid.Obfuscate

@Suppress("unused")
@Obfuscate
class ActivityInfoSelector(
    val context: Context, private val multiMode: Boolean,
    private val activitys: Array<ActivityInfo>?
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

    private var allActivityInfos = ArrayList<ActivityInfo>()
    private var allEnabledInfos = ArrayList<ActivityInfo>()
    private var enabledList = ArrayList<String>()

    private var onSelectActivityInfoListener: OnSelectActivityInfoListener? = null

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
                onSelectActivityInfoListener?.resultSelectActivityInfos(infos)
            }
        }
    }

    fun show() {
        if (allActivityInfos.isEmpty()) loadData()

        dialog = dialogBuilder.show()
    }

    fun setOnSelectActivityListener(onSelectActivityInfoListener: OnSelectActivityInfoListener) {
        this.onSelectActivityInfoListener = onSelectActivityInfoListener
    }

    fun setEnabledList(list: ArrayList<String>) {
        enabledList = list
    }

    private fun loadData() {
        scope {
            allActivityInfos.clear()
            allEnabledInfos.clear()

            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null

            withDefault {
                allActivityInfos.addAll(activitys?.toList() ?: arrayListOf())
                allActivityInfos.forEach {
                    if (enabledList.contains(it.packageName)) allEnabledInfos.add(it)
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

        private var filterDatas = ArrayList<ActivityInfo>()

        init {
            filterDatas.clear()
            filterDatas = allActivityInfos
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
            val activityInfo = filterDatas[position]
            val appIcon = activityInfo.loadIcon(context.packageManager)
            val label = activityInfo.loadLabel(context.packageManager)
            val name = activityInfo.name

            holder.activityIcon.setImageDrawable(appIcon)
            holder.activityLabel.text = label
            holder.activityName.text = name
            holder.activityInfoView.setOnClickListener(null)

            holder.activityInfoView.setOnClickListener {
                dialog.dismiss()
                onSelectActivityInfoListener?.resultSelectActivityInfos(arrayListOf(activityInfo))
            }
        }

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterStr = constraint.toString().lowercase()
                filterDatas = if (constraint.isBlank()) allActivityInfos
                else {
                    val filterlist = ArrayList<ActivityInfo>()
                    allActivityInfos.forEach {
                        val label = it.loadLabel(context.packageManager)
                        if (it.name.lowercase().contains(filterStr)
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
                filterDatas = results.values as ArrayList<ActivityInfo>
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

        private var filterDatas = ArrayList<ActivityInfo>()
        private var enabledDatas = ArrayList<ActivityInfo>()

        init {
            filterDatas.clear()
            enabledDatas.clear()

            filterDatas = allActivityInfos

            allEnabledInfos.forEach {
                enabledDatas.add(it)
                filterDatas.remove(it)
            }

            filterDatas.addAll(0, enabledDatas)
        }

        fun getEnabledInfos(): ArrayList<ActivityInfo> {
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
            val activityInfo = filterDatas[position]
            val appIcon = activityInfo.loadIcon(context.packageManager)
            val label = activityInfo.loadLabel(context.packageManager)
            val name = activityInfo.name

            holder.activityIcon.setImageDrawable(appIcon)
            holder.activityLabel.text = label
            holder.activityName.text = name
            holder.activityInfoView.setOnClickListener(null)
            holder.checkbox.setOnCheckedChangeListener(null)

            holder.checkbox.isChecked = enabledDatas.contains(activityInfo)
            holder.activityInfoView.setOnClickListener {
                holder.checkbox.performClick()
            }
            holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                enabledDatas.remove(activityInfo)
                if (isChecked) enabledDatas.add(activityInfo)
            }
        }

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterStr = constraint.toString().lowercase()
                filterDatas = if (constraint.isBlank()) allActivityInfos
                else {
                    val filterlist = ArrayList<ActivityInfo>()
                    allActivityInfos.forEach {
                        val label = it.loadLabel(context.packageManager)
                        if (it.name.lowercase().contains(filterStr)
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
                filterDatas = results.values as ArrayList<ActivityInfo>
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