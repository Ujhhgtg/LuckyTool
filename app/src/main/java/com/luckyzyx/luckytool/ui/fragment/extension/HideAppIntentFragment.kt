package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
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
import android.widget.ImageView
import android.widget.TextView
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
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.data.AppIntentInfo
import com.luckyzyx.luckytool.databinding.FragmentHideIntentApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutIntentAppinfoSwitchItemBinding
import com.luckyzyx.luckytool.listener.OnSelectIntentInfoListener
import com.luckyzyx.luckytool.selector.IntentInfoSelector
import com.luckyzyx.luckytool.selector.SortFilterSelector
import com.luckyzyx.luckytool.utils.IntentPrefs
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.RestartMenuUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.safeOf
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

    private val scopes = arrayOf("com.android.intentresolver")

    private var allAppInfos = ArrayList<AppInfo>()
    private var allResolveInfos = ArrayList<AppIntentInfo>()
    private var allEnabledInfos = ArrayList<AppIntentInfo>()

    private var allSingleShareIntents = ArrayList<ResolveInfo>()
    private var allMultiShareIntents = ArrayList<ResolveInfo>()
    private var allTextIntents = ArrayList<ResolveInfo>()
    private var allOpenIntents = ArrayList<ResolveInfo>()
    private var allHttpLinkIntents = ArrayList<ResolveInfo>()
    private var allHttpsLinkIntents = ArrayList<ResolveInfo>()

    private val shareListKey = "share_app_hide_list"
    private val textListKey = "text_app_hide_list"
    private val openListKey = "open_app_hide_list"
    private val browserListKey = "browser_app_hide_list"

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
            setFilterChips(false, arrayOf())
        }

        val configKeys = arrayOf(
            "enable_share_intent_switch",
            "enable_text_intent_switch",
            "enable_open_intent_switch",
            "enable_browser_intent_switch"
        )
        val configTitles = arrayOf(
            getString(R.string.enable_share_intent_switch) + "(${getString(R.string.need_restart_scope)})",
            getString(R.string.enable_text_intent_switch) + "(等待施工)",
            getString(R.string.enable_open_intent_switch) + "(等待施工)",
            getString(R.string.enable_browser_intent_switch) + "(等待施工)"
        )
        val configValues = booleanArrayOf(
            context.getBoolean(IntentPrefs, configKeys[0], false),
            context.getBoolean(IntentPrefs, configKeys[1], false),
            context.getBoolean(IntentPrefs, configKeys[2], false),
            context.getBoolean(IntentPrefs, configKeys[3], false)
        )
        binding.configIntentList.apply {
            setOnClickListener {
                MaterialAlertDialogBuilder(context, dialogCentered).apply {
                    setMultiChoiceItems(configTitles, configValues) { _, which, isChecked ->
                        context.putBoolean(IntentPrefs, configKeys[which], isChecked)
                    }
                    show()
                }
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
                appIntentAdapter?.getFilter?.filter(text)
            })
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                loadData()
            }
        }

        if (allAppInfos.isEmpty() || allResolveInfos.isEmpty()) loadData()
    }

    private fun loadData() {
        val context = requireContext()

        scopeLife {
            allAppInfos.clear()
            allResolveInfos.clear()
            allEnabledInfos.clear()

            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null

            withDefault {
                val existIntentApps = ArraySet<String>()

                val packageManager = requireActivity().packageManager

                allSingleShareIntents = ArrayList(
                    packageManager.queryIntentActivities(
                        Intent(Intent.ACTION_SEND).setType("*/*"),
                        PackageManager.MATCH_ALL
                    ).onEach {
                        existIntentApps.add(it.activityInfo.packageName)
                        allResolveInfos.add(
                            AppIntentInfo(
                                it.loadLabel(packageManager),
                                it.activityInfo.packageName, Intent.ACTION_SEND,
                                "single_share", it
                            )
                        )
                    })
                allMultiShareIntents = ArrayList(
                    packageManager.queryIntentActivities(
                        Intent(Intent.ACTION_SEND_MULTIPLE).setType("*/*"),
                        PackageManager.MATCH_ALL
                    ).onEach {
                        existIntentApps.add(it.activityInfo.packageName)
                        allResolveInfos.add(
                            AppIntentInfo(
                                it.loadLabel(packageManager),
                                it.activityInfo.packageName, Intent.ACTION_SEND_MULTIPLE,
                                "multi_share", it
                            )
                        )
                    })
                allTextIntents = ArrayList(
                    packageManager.queryIntentActivities(
                        Intent(Intent.ACTION_PROCESS_TEXT).setType("*/*"),
                        PackageManager.MATCH_ALL
                    ).onEach {
                        existIntentApps.add(it.activityInfo.packageName)
                        allResolveInfos.add(
                            AppIntentInfo(
                                it.loadLabel(packageManager),
                                it.activityInfo.packageName, Intent.ACTION_PROCESS_TEXT,
                                "process_text", it
                            )
                        )
                    })
                allOpenIntents = ArrayList(
                    packageManager.queryIntentActivities(
                        Intent(Intent.ACTION_VIEW).setDataAndType(
                            "content://${BuildConfig.APPLICATION_ID}.FileProvider".toUri(), "*/*"
                        ),
                        PackageManager.MATCH_ALL
                    ).onEach {
                        existIntentApps.add(it.activityInfo.packageName)
                        allResolveInfos.add(
                            AppIntentInfo(
                                it.loadLabel(packageManager),
                                it.activityInfo.packageName, Intent.ACTION_VIEW,
                                "content_view", it
                            )
                        )
                    })
                allHttpLinkIntents = ArrayList(
                    packageManager.queryIntentActivities(
                        Intent(Intent.ACTION_VIEW).setDataAndType("http://".toUri(), "*/*"),
                        PackageManager.MATCH_ALL
                    ).onEach {
                        existIntentApps.add(it.activityInfo.packageName)
                        allResolveInfos.add(
                            AppIntentInfo(
                                it.loadLabel(packageManager),
                                it.activityInfo.packageName, Intent.ACTION_VIEW,
                                "http_link", it
                            )
                        )
                    })
                allHttpsLinkIntents = ArrayList(
                    packageManager.queryIntentActivities(
                        Intent(Intent.ACTION_VIEW).setDataAndType("https://".toUri(), "*/*"),
                        PackageManager.MATCH_ALL
                    ).onEach {
                        existIntentApps.add(it.activityInfo.packageName)
                        allResolveInfos.add(
                            AppIntentInfo(
                                it.loadLabel(packageManager),
                                it.activityInfo.packageName, Intent.ACTION_VIEW,
                                "https_link", it
                            )
                        )
                    })

                val enabledShareApps = context.getStringSet(IntentPrefs, shareListKey, ArraySet())
                val enabledTextApps = context.getStringSet(IntentPrefs, textListKey, ArraySet())
                val enabledOpenApps = context.getStringSet(IntentPrefs, openListKey, ArraySet())
                val enabledBrowserApps =
                    context.getStringSet(IntentPrefs, browserListKey, ArraySet())

                val sortList = ArrayList<AppInfo>()
                existIntentApps.forEachIndexed { _, packName ->
                    val info = PackageUtils(packageManager).getInstalledAppInfo(packName, 0)
                        ?: return@forEachIndexed
                    if (info.isSystem) return@forEachIndexed

                    if (enabledShareApps.contains(packName)
                        || enabledTextApps.contains(packName)
                        || enabledOpenApps.contains(packName)
                        || enabledBrowserApps.contains(packName)
                    ) sortList.add(info)
                    else allAppInfos.add(info)

                    context.getStringSet(IntentPrefs, packName, ArraySet()).apply {
                        forEachIndexed { _, js ->
                            val jsonObject = safeOf(JSONObject()) { JSONObject(js) }
                            val action = jsonObject.optString("action", "")
                            val type = jsonObject.optString("type", "")
                            val activity = jsonObject.optString("activity", "")
                            val find = allResolveInfos.find {
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
            }

            binding.recyclerView.apply {
                appIntentAdapter = AppIntentAdapter()
                adapter = appIntentAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }

            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchViewLayout.isEnabled = true
        }
    }

    fun showUpdateAppIntent(
        packName: String, type: String, allInfos: List<AppIntentInfo>, enabled: List<AppIntentInfo>
    ) {
        IntentInfoSelector(requireActivity(), true, ArrayList(allInfos)).apply {
            setEnabledList(ArrayList(enabled))
            setOnSelectIntentInfoListener(object : OnSelectIntentInfoListener {
                override fun resultSelectIntentInfos(list: ArrayList<AppIntentInfo>) {
                    updateAppIntentList(packName, type, list)
                }
            })
            show()
        }
    }

    fun updateAppIntentList(packName: String, type: String, list: ArrayList<AppIntentInfo>) {
        val context = requireContext()
        val listKey = when (type) {
            "shareList" -> shareListKey
            "textList" -> textListKey
            "openList" -> openListKey
            "browserList" -> browserListKey
            else -> ""
        }
        if (listKey.isNotBlank()) {
            val enabledShareApps = context.getStringSet(IntentPrefs, listKey, ArraySet())
            val newList = enabledShareApps.toMutableList().apply {
                if (list.isEmpty()) remove(packName) else add(packName)
            }
            context.putStringSet(IntentPrefs, listKey, newList.toSet())
        }
        val jsonArray = JSONArray().apply {
            list.forEach {
                put(it.toJSONObject())
            }
        }
        context.putStringSet(IntentPrefs, packName, jsonArray.toStringList().toSet())
        loadData()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.menu_reboot)).apply {
            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            1 -> RestartMenuUtils.showRestartScopeDialog(requireActivity(), scopes)
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = LayoutIntentAppinfoSwitchItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val appInfo = filterDatas[position]
            val appIcon = appInfo.icon
            val appName = appInfo.name
            val packName = appInfo.packageName

            val intentInfo = allResolveInfos.filter { it.packName == packName }
            val enabledInfo = allEnabledInfos.filter { it.packName == packName }

            holder.appIcon.setImageDrawable(appIcon)
            holder.appName.text = appName
            holder.packName.text = packName

            holder.shareBtn.apply {
                val allIntent = intentInfo.filter {
                    it.action == Intent.ACTION_SEND || it.action == Intent.ACTION_SEND_MULTIPLE
                }
                val enabled = enabledInfo.filter {
                    it.action == Intent.ACTION_SEND || it.action == Intent.ACTION_SEND_MULTIPLE
                }
                text = "${enabled.size}/${allIntent.size}"
                setOnClickListener(null)
                if (allIntent.isNotEmpty()) {
                    setOnClickListener {
                        showUpdateAppIntent(packName, "shareList", allIntent, enabled)
                    }
                }
                if (enabled.isNotEmpty()) {
                    isCheckable = true
                    isChecked = true
                    isCheckable = false
                }
            }
            holder.textBtn.apply {
                val allIntent = intentInfo.filter { it.action == Intent.ACTION_PROCESS_TEXT }
                val enabled = enabledInfo.filter { it.action == Intent.ACTION_PROCESS_TEXT }
                text = "${enabled.size}/${allIntent.size}"
                setOnClickListener(null)
                if (allIntent.isNotEmpty()) {
                    setOnClickListener {
                        showUpdateAppIntent(packName, "textList", allIntent, enabled)
                    }
                }
                if (enabled.isNotEmpty()) {
                    isCheckable = true
                    isChecked = true
                    isCheckable = false
                }
            }
            holder.openBtn.apply {
                val allIntent = intentInfo.filter {
                    it.action == Intent.ACTION_VIEW && it.type == "content_view"
                }
                val enabled = enabledInfo.filter {
                    it.action == Intent.ACTION_VIEW && it.type == "content_view"
                }
                text = "${enabled.size}/${allIntent.size}"
                setOnClickListener(null)
                if (allIntent.isNotEmpty()) {
                    setOnClickListener {
                        showUpdateAppIntent(packName, "openList", allIntent, enabled)
                    }
                }
                if (enabled.isNotEmpty()) {
                    isCheckable = true
                    isChecked = true
                    isCheckable = false
                }
            }
            holder.browserBtn.apply {
                val allIntent = intentInfo.filter {
                    it.action == Intent.ACTION_VIEW && (it.type == "http_link" || it.type == "https_link")
                }
                val enabled = enabledInfo.filter {
                    it.action == Intent.ACTION_VIEW && (it.type == "http_link" || it.type == "https_link")
                }
                text = "${enabled.size}/${allIntent.size}"
                setOnClickListener(null)
                if (allIntent.isNotEmpty()) {
                    setOnClickListener {
                        showUpdateAppIntent(packName, "browserList", allIntent, enabled)
                    }
                }
                if (enabled.isNotEmpty()) {
                    isCheckable = true
                    isChecked = true
                    isCheckable = false
                }
            }
        }

        override fun getItemCount(): Int = filterDatas.size

        val getFilter = object : Filter() {
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

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
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