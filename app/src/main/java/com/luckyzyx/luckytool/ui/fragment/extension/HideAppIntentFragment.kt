package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.collection.arraySetOf
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.data.AppIntentInfo
import com.luckyzyx.luckytool.databinding.FragmentHideIntentApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutIntentAppinfoSwitchItemBinding
import com.luckyzyx.luckytool.enums.IntentType
import com.luckyzyx.luckytool.listener.OnSelectIntentInfoListener
import com.luckyzyx.luckytool.selector.IntentInfoSelector
import com.luckyzyx.luckytool.selector.SortFilterSelector
import com.luckyzyx.luckytool.utils.IntentPrefs
import com.luckyzyx.luckytool.utils.IntentUtils.Companion.getFilterType
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.clearPrefs
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.removeKey
import com.luckyzyx.luckytool.utils.safeOf
import com.luckyzyx.luckytool.utils.safeOfNull
import com.luckyzyx.luckytool.utils.sendPrefsValue
import com.luckyzyx.luckytool.utils.setupMenuProvider
import com.luckyzyx.luckytool.utils.toStringList
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import org.json.JSONArray
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class HideAppIntentFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentHideIntentApplistLayoutBinding
    private var appIntentAdapter: AppIntentAdapter? = null
    private lateinit var sortFilterSelector: SortFilterSelector
    private var isReverse = false
    private var sortMode = 0
    private var showSystemApps = true
    private var intentFilter = ArraySet<IntentType>()

    private var allAppInfos = ArrayList<AppInfo>()
    private var allIntentInfos = ArrayList<AppIntentInfo>()
    private var allEnabledInfos = ArrayList<AppIntentInfo>()

    private var allIntentFilter = ArrayMap<IntentType, Intent>()

    private val isEnableKey = "custom_config_app_intent_list"
    private val enabledListKey = "enable_app_hide_list"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        binding = FragmentHideIntentApplistLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()
        sortFilterSelector = SortFilterSelector(context).apply {
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
            setFilterChips(
                true, arrayOf(
                    Chip(context).apply {
                        text = context.getString(R.string.appinfo_system_app)
                        isCheckable = true
                        isClickable = true
                        isChecked = showSystemApps
                        setOnCheckedChangeListener { _, isChecked ->
                            showSystemApps = isChecked
                            loadData()
                        }
                    },
                    Chip(context).apply {
                        val types = arraySetOf(IntentType.SINGLE_SHARE, IntentType.MULTI_SHARE)
                        text = getString(R.string.intent_share)
                        isCheckable = true
                        isClickable = true
                        isChecked = true
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) intentFilter.addAll(types)
                            else intentFilter.removeAll(types)
                            loadData()
                        }
                    },
                    Chip(context).apply {
                        val types = arraySetOf(IntentType.PROCESS_TEXT)
                        text = getString(R.string.intent_text)
                        isCheckable = true
                        isClickable = true
                        isChecked = true
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) intentFilter.addAll(types)
                            else intentFilter.removeAll(types)
                            loadData()
                        }
                    },
                    Chip(context).apply {
                        val types = arraySetOf(IntentType.CONTENT, IntentType.FILE)
                        text = getString(R.string.intent_open)
                        isCheckable = true
                        isClickable = true
                        isChecked = true
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) intentFilter.addAll(types)
                            else intentFilter.removeAll(types)
                            loadData()
                        }
                    },
                    Chip(context).apply {
                        val types = arraySetOf(IntentType.HTTP_LINK, IntentType.HTTPS_LINK)
                        text = getString(R.string.intent_browser)
                        isCheckable = true
                        isClickable = true
                        isChecked = true
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) intentFilter.addAll(types)
                            else intentFilter.removeAll(types)
                            loadData()
                        }
                    }
                )
            )
        }

        binding.configIntentList.apply {
            isChecked = context.getBoolean(IntentPrefs, isEnableKey, false)
            setOnCheckedChangeListener { _, isChecked ->
                context.putBoolean(IntentPrefs, isEnableKey, isChecked)
                context.sendPrefsValue("android", isEnableKey, isChecked)
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
                appIntentAdapter?.appFilter?.filter(text)
            })
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                loadData()
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            FastScrollerBuilder(this).useMd2Style().build()
        }

        if (allAppInfos.isEmpty() || allIntentInfos.isEmpty()) loadData()
    }

    private fun loadData() {
        val context = requireContext()

        scopeLife {
            allAppInfos.clear()
            allIntentInfos.clear()
            allEnabledInfos.clear()

            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null

            withDefault {
                val existIntentApps = ArraySet<String>()

                val packageManager = requireActivity().packageManager

                allIntentFilter = arrayMapOf(
                    IntentType.SINGLE_SHARE to Intent(Intent.ACTION_SEND),
                    IntentType.MULTI_SHARE to Intent(Intent.ACTION_SEND_MULTIPLE),
                    IntentType.PROCESS_TEXT to Intent(Intent.ACTION_PROCESS_TEXT),
                    IntentType.CONTENT to Intent().setDataAndType("content://".toUri(), "*/*"),
                    IntentType.FILE to Intent().setDataAndType("file://".toUri(), "*/*"),
                    IntentType.HTTP_LINK to Intent().setDataAndType("http://".toUri(), "*/*"),
                    IntentType.HTTPS_LINK to Intent().setDataAndType("https://".toUri(), "*/*"),
                )

                if (intentFilter.isEmpty()) intentFilter.addAll(allIntentFilter.map { it.key })

                allIntentFilter.forEach { (type, intent) ->
                    if (intent.action == null) intent.setAction(Intent.ACTION_VIEW)
                    if (intent.data == null) intent.setType("*/*")
                    intent.putExtra("result_origin_data", true)

                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL).onEach {
                        existIntentApps.add(it.activityInfo.packageName)
                        allIntentInfos.add(
                            AppIntentInfo(
                                it.loadLabel(packageManager), it.activityInfo.packageName,
                                intent.action!!, type, it
                            )
                        )
                    }
                }

                val enabledApps = context.getStringSet(IntentPrefs, enabledListKey, ArraySet())

                val sortList = ArrayList<AppInfo>()
                existIntentApps.forEachIndexed { _, packName ->
                    val info = PackageUtils(packageManager).getInstalledAppInfo(packName, 0)
                        ?: return@forEachIndexed

                    if (!showSystemApps && info.isSystem) {
                        allIntentInfos.removeIf { it.packName == packName }
                        return@forEachIndexed
                    }

                    if (enabledApps.contains(packName)) sortList.add(info)
                    else allAppInfos.add(info)

                    context.getStringSet(IntentPrefs, packName, ArraySet()).apply {
                        forEachIndexed { _, js ->
                            val jsonObject = safeOf(JSONObject()) { JSONObject(js) }
                            val action = jsonObject.optString("action")
                            val type = IntentType.fromString(jsonObject.optString("type"))
                            val activity = jsonObject.optString("activity")
                            val find = allIntentInfos.find {
                                it.action == action && it.type == type && it.packName == packName
                                        && it.resolveInfo.activityInfo.name == activity
                            }
                            if (find != null) allEnabledInfos.add(find)
                        }
                    }
                }
                sortList.apply {
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
                    addAll(0, sortList)
                }

                if (intentFilter.isNotEmpty()) setIntentTypeFilter()

            }

            appIntentAdapter = AppIntentAdapter()
            binding.recyclerView.adapter = appIntentAdapter

            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    private fun setIntentTypeFilter() {
        allIntentInfos.removeIf { !intentFilter.contains(it.type) }
        allEnabledInfos.removeIf { !intentFilter.contains(it.type) }
        allAppInfos.removeIf { info ->
            !allIntentInfos.map { it.packName }.contains(info.packageName)
        }
    }

    fun updateAppIntent(
        packName: String, allInfos: List<AppIntentInfo>,
        enabled: List<AppIntentInfo>, types: Array<IntentType>, position: Int
    ) {
        IntentInfoSelector(requireActivity(), true, ArrayList(allInfos)).apply {
            setEnabledList(ArrayList(enabled))
            setSelectAllMode(true)
            setOnSelectIntentInfoListener(object : OnSelectIntentInfoListener {
                override fun resultSelectIntentInfos(list: ArrayList<AppIntentInfo>) {
                    saveAppIntentList(packName, list, *types)
                    saveEnabledAppList(packName, list)
                    if (position >= 0) appIntentAdapter?.refreshPosition(position)
                }
            })
            show()
        }
    }

    private fun selectAllInfos(vararg type: IntentType) {
        val filte = getFilterType(*type)
        val allIntents = allIntentInfos.filter(filte)
        val enabledIntents = allEnabledInfos.filter(filte)
        val isAll = allIntents.size == enabledIntents.size
        allIntents.map { it.packName }.forEachIndexed { _, packName ->
            if (isAll) {
                saveAppIntentList(packName, arrayListOf(), *type)
                saveEnabledAppList(packName, arrayListOf())
            } else {
                val intents = allIntents.filter { it.packName == packName }
                saveAppIntentList(packName, ArrayList(intents), *type)
                saveEnabledAppList(packName, ArrayList(intents))
            }
            appIntentAdapter?.refreshDatas()
        }
    }

    fun saveAppIntentList(
        packName: String, list: ArrayList<AppIntentInfo>, vararg types: IntentType
    ) {
        val context = requireContext()
        val filte = getFilterType(*types)
        val appIntents = ArrayList<AppIntentInfo>().apply {
            context.getStringSet(IntentPrefs, packName, ArraySet()).forEachIndexed { _, js ->
                val jsonObject = safeOfNull { JSONObject(js) } ?: return@forEachIndexed
                val intent = AppIntentInfo().toAppIntentInfo(jsonObject)
                add(intent)
            }
        }
        appIntents.removeIf(filte)
        if (list.isNotEmpty()) appIntents.addAll(list)

        allEnabledInfos.removeIf { it.packName == packName }
        allEnabledInfos.addAll(appIntents)
        if (appIntents.isNotEmpty()) setIntentTypeFilter()

        if (appIntents.isEmpty()) {
            context.removeKey(IntentPrefs, packName)
            return
        }

        val arrays = appIntents.map { it.toJSONObject() }
        val jsonArray = safeOf(JSONArray()) { JSONArray(arrays) }
        context.putStringSet(IntentPrefs, packName, jsonArray.toStringList().toSet())
    }

    fun saveEnabledAppList(packName: String, list: ArrayList<AppIntentInfo>) {
        val context = requireContext()
        val enabledApps = context.getStringSet(IntentPrefs, enabledListKey, ArraySet())
        val intents = context.getStringSet(IntentPrefs, packName, ArraySet())
        val newList = ArraySet(enabledApps).apply {
            remove(packName)
            if (list.isNotEmpty() || intents.isNotEmpty()) add(packName)
        }
        context.putStringSet(IntentPrefs, enabledListKey, newList.toSet())
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.select_all_share_intent)).apply {
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        }
        menu.add(0, 2, 0, getString(R.string.select_all_text_intent)).apply {
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        }
        menu.add(0, 3, 0, getString(R.string.select_all_open_intent)).apply {
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        }
        menu.add(0, 4, 0, getString(R.string.select_all_browser_intent)).apply {
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        }
        menu.add(0, 10, 0, getString(R.string.clear_all_data)).apply {
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            1 -> selectAllInfos(IntentType.SINGLE_SHARE, IntentType.MULTI_SHARE)
            2 -> selectAllInfos(IntentType.PROCESS_TEXT)
            3 -> selectAllInfos(IntentType.CONTENT, IntentType.FILE)
            4 -> selectAllInfos(IntentType.HTTP_LINK, IntentType.HTTPS_LINK)
            10 -> MaterialAlertDialogBuilder(requireContext()).apply {
                setNeutralButton(android.R.string.cancel, null)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    context.clearPrefs(IntentPrefs)
                    loadData()
                }
                show()
            }
        }
        return true
    }

    @Obfuscate
    inner class AppIntentAdapter : RecyclerView.Adapter<ViewHolder>() {

        private var filterDatas = ArrayList<AppInfo>()

        init {
            filterDatas.clear()
            filterDatas = allAppInfos
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val appInfo = filterDatas[position]
            val appIcon = appInfo.icon
            val appName = appInfo.name
            val packName = appInfo.packageName

            val intentInfo = allIntentInfos.filter { it.packName == packName }
            val enabledInfo = allEnabledInfos.filter { it.packName == packName }

            holder.appIcon.setImageDrawable(appIcon)
            holder.appName.text = appName
            holder.packName.text = packName

            holder.shareBtn.apply {
                val types = arrayOf(IntentType.SINGLE_SHARE, IntentType.MULTI_SHARE)
                val curFilter = getFilterType(*types)
                val allIntent = intentInfo.filter(curFilter)
                val enabled = enabledInfo.filter(curFilter)
                text = "${enabled.size}/${allIntent.size}"

                isCheckable = true
                isChecked = enabled.isNotEmpty()
                isCheckable = false

                setOnClickListener(null)
                if (allIntent.isNotEmpty()) {
                    setOnClickListener {
                        updateAppIntent(packName, allIntent, enabled, types, position)
                    }
                }
            }
            holder.textBtn.apply {
                val types = arrayOf(IntentType.PROCESS_TEXT)
                val curFilter = getFilterType(*types)
                val allIntent = intentInfo.filter(curFilter)
                val enabled = enabledInfo.filter(curFilter)
                text = "${enabled.size}/${allIntent.size}"

                isCheckable = true
                isChecked = enabled.isNotEmpty()
                isCheckable = false

                setOnClickListener(null)
                if (allIntent.isNotEmpty()) {
                    setOnClickListener {
                        updateAppIntent(packName, allIntent, enabled, types, position)
                    }
                }
            }
            holder.openBtn.apply {
                val types = arrayOf(IntentType.CONTENT, IntentType.FILE)
                val curFilter = getFilterType(*types)
                val allIntent = intentInfo.filter(curFilter)
                val enabled = enabledInfo.filter(curFilter)
                text = "${enabled.size}/${allIntent.size}"

                isCheckable = true
                isChecked = enabled.isNotEmpty()
                isCheckable = false

                setOnClickListener(null)
                if (allIntent.isNotEmpty()) {
                    setOnClickListener {
                        updateAppIntent(packName, allIntent, enabled, types, position)
                    }
                }
            }
            holder.browserBtn.apply {
                val types = arrayOf(IntentType.HTTP_LINK, IntentType.HTTPS_LINK)
                val curFilter = getFilterType(*types)
                val allIntent = intentInfo.filter(curFilter)
                val enabled = enabledInfo.filter(curFilter)
                text = "${enabled.size}/${allIntent.size}"

                isCheckable = true
                isChecked = enabled.isNotEmpty()
                isCheckable = false

                setOnClickListener(null)
                if (allIntent.isNotEmpty()) {
                    setOnClickListener {
                        updateAppIntent(packName, allIntent, enabled, types, position)
                    }
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
        }

        fun refreshPosition(position: Int) {
            notifyItemChanged(position)
        }

        val appFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterStr = constraint.toString().lowercase()
                filterDatas = if (constraint.isBlank()) allAppInfos
                else {
                    val filterlist = ArrayList<AppInfo>()
                    allAppInfos.forEach {
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

        override fun getItemCount(): Int = filterDatas.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = LayoutIntentAppinfoSwitchItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }
    }

    @Obfuscate
    class ViewHolder(binding: LayoutIntentAppinfoSwitchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val appIcon: ImageView = binding.appIcon
        val appName: TextView = binding.appName
        val packName: TextView = binding.packName
        val shareBtn: Chip = binding.shareIntentChip
        val textBtn: Chip = binding.textIntentChip
        val openBtn: Chip = binding.openIntentChip
        val browserBtn: Chip = binding.browserIntentChip
    }
}