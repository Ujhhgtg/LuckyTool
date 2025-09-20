package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.collection.arraySetOf
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.betterandroid.ui.component.adapter.factory.bindAdapter
import com.highcapable.betterandroid.ui.component.adapter.recycler.factory.notifyDataSetChangedIgnore
import com.highcapable.betterandroid.ui.component.adapter.recycler.factory.wrapper
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.AppInfo
import com.luckyzyx.luckytool.data.AppIntentInfo
import com.luckyzyx.luckytool.databinding.FragmentHideIntentApplistLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutIntentAppinfoSwitchItemBinding
import com.luckyzyx.luckytool.enums.IntentType
import com.luckyzyx.luckytool.listener.OnSelectIntentInfoListener
import com.luckyzyx.luckytool.selector.IntentInfoSelectDialog
import com.luckyzyx.luckytool.selector.SortFilterBottomSheetDialog
import com.luckyzyx.luckytool.ui.fragment.base.BaseFragment
import com.luckyzyx.luckytool.utils.IntentPrefs
import com.luckyzyx.luckytool.utils.IntentUtils.Companion.getIntentFilter
import com.luckyzyx.luckytool.utils.PackageUtils
import com.luckyzyx.luckytool.utils.clearPrefs
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getStringSet
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putStringSet
import com.luckyzyx.luckytool.utils.removeKey
import com.luckyzyx.luckytool.utils.safeOfNull
import com.luckyzyx.luckytool.utils.sendPrefsValue
import com.luckyzyx.luckytool.utils.setupMenuProvider
import kotlinx.serialization.json.Json
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
class HideAppIntentFragment : BaseFragment<FragmentHideIntentApplistLayoutBinding>(), MenuProvider {

    private val TAG = "HideAppIntentFragment"

    private lateinit var sortFilterBottomSheetDialog: SortFilterBottomSheetDialog

    private var isReverse = false
    private var sortMode = 0
    private var showSystemApps = true
    private var intentFilter = ArraySet<IntentType>()

    private var allAppInfos = ArrayList<AppInfo>()
    private var filterAppInfos = ArrayList<AppInfo>()
    private var allResolveInfoMap = ArrayMap<AppIntentInfo, ResolveInfo>()
    private var allIntentInfos = ArrayList<AppIntentInfo>()
    private var allEnabledInfos = ArrayList<AppIntentInfo>()

    private var allIntentFilter = ArrayMap<IntentType, Intent>()

    private val isEnableKey = "custom_config_app_intent_list"
    private val enabledListKey = "enable_app_hide_list"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setupMenuProvider(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireActivity()
        sortFilterBottomSheetDialog = SortFilterBottomSheetDialog(context).apply {
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
                sortFilterBottomSheetDialog.show()
            }
        }
        binding.searchView.apply {
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                val query = text?.toString() ?: ""
                filterAppInfos = if (query.isBlank()) allAppInfos
                else {
                    val newList = allAppInfos.filter {
                        it.name.contains(query) ||
                                it.packageName.lowercase().contains(query)
                    }
                    ArrayList(newList)
                }
                binding.recyclerView.adapter?.notifyDataSetChangedIgnore()
            })
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                loadData()
            }
        }

        binding.recyclerView.apply {
            adapter = bindAdapter<AppInfo> {
                onBindData { filterAppInfos }
                onBindItemView<LayoutIntentAppinfoSwitchItemBinding> { item, info, position ->
                    val intentInfo = allIntentInfos.filter { it.packName == info.packageName }
                    val enabledInfo = allEnabledInfos.filter { it.packName == info.packageName }

                    item.appIcon.setImageDrawable(info.icon)
                    item.appName.text = info.name
                    item.packName.text = info.packageName

                    item.shareIntentChip.apply {
                        val types = arrayOf(IntentType.SINGLE_SHARE, IntentType.MULTI_SHARE)
                        val curFilter = getIntentFilter(*types)
                        val allIntent = intentInfo.filter(curFilter)
                        val enabled = enabledInfo.filter(curFilter)
                        text = "${enabled.size}/${allIntent.size}"

                        isCheckable = true
                        isChecked = enabled.isNotEmpty()
                        isCheckable = false

                        setOnClickListener(null)
                        if (allIntent.isNotEmpty()) {
                            setOnClickListener {
                                updateAppIntent(
                                    info.packageName, allIntent,
                                    enabled, types, position.value
                                )
                            }
                        }
                    }
                    item.textIntentChip.apply {
                        val types = arrayOf(IntentType.PROCESS_TEXT)
                        val curFilter = getIntentFilter(*types)
                        val allIntent = intentInfo.filter(curFilter)
                        val enabled = enabledInfo.filter(curFilter)
                        text = "${enabled.size}/${allIntent.size}"

                        isCheckable = true
                        isChecked = enabled.isNotEmpty()
                        isCheckable = false

                        setOnClickListener(null)
                        if (allIntent.isNotEmpty()) {
                            setOnClickListener {
                                updateAppIntent(
                                    info.packageName, allIntent,
                                    enabled, types, position.value
                                )
                            }
                        }
                    }
                    item.openIntentChip.apply {
                        val types = arrayOf(IntentType.CONTENT, IntentType.FILE)
                        val curFilter = getIntentFilter(*types)
                        val allIntent = intentInfo.filter(curFilter)
                        val enabled = enabledInfo.filter(curFilter)
                        text = "${enabled.size}/${allIntent.size}"

                        isCheckable = true
                        isChecked = enabled.isNotEmpty()
                        isCheckable = false

                        setOnClickListener(null)
                        if (allIntent.isNotEmpty()) {
                            setOnClickListener {
                                updateAppIntent(
                                    info.packageName, allIntent,
                                    enabled, types, position.value
                                )
                            }
                        }
                    }
                    item.browserIntentChip.apply {
                        val types = arrayOf(IntentType.HTTP_LINK, IntentType.HTTPS_LINK)
                        val curFilter = getIntentFilter(*types)
                        val allIntent = intentInfo.filter(curFilter)
                        val enabled = enabledInfo.filter(curFilter)
                        text = "${enabled.size}/${allIntent.size}"

                        isCheckable = true
                        isChecked = enabled.isNotEmpty()
                        isCheckable = false

                        setOnClickListener(null)
                        if (allIntent.isNotEmpty()) {
                            setOnClickListener {
                                updateAppIntent(
                                    info.packageName, allIntent,
                                    enabled, types, position.value
                                )
                            }
                        }
                    }
                }
            }
            FastScrollerBuilder(this).useMd2Style().build()
        }

        if (allAppInfos.isEmpty() || allIntentInfos.isEmpty()) loadData()
    }

    private fun loadData() {
        val context = requireActivity()

        scopeLife {
            allAppInfos.clear()
            filterAppInfos.clear()
            allIntentInfos.clear()
            allEnabledInfos.clear()

            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchViewLayout.isEnabled = false
            binding.searchView.text = null

            withDefault {
                val existIntentApps = ArraySet<String>()

                val packageManager = requireActivity().packageManager
                val packageUtils = PackageUtils(packageManager)

                allIntentFilter = arrayMapOf(
                    IntentType.SINGLE_SHARE to Intent(Intent.ACTION_SEND),
                    IntentType.MULTI_SHARE to Intent(Intent.ACTION_SEND_MULTIPLE),
                    IntentType.PROCESS_TEXT to Intent(Intent.ACTION_PROCESS_TEXT),
                    IntentType.CONTENT to Intent().setDataAndType("content://".toUri(), "*/*"),
                    IntentType.FILE to Intent().setDataAndType("file://".toUri(), "*/*"),
                    IntentType.HTTP_LINK to Intent().setDataAndType("http://".toUri(), "*/*"),
                    IntentType.HTTPS_LINK to Intent().setDataAndType("https://".toUri(), "*/*"),
                ).onEach {
                    if (it.value.action == null) it.value.setAction(Intent.ACTION_VIEW)
                    if (it.value.data == null) it.value.setType("*/*")
                    it.value.putExtra("result_origin_data", true)
                }

                if (intentFilter.isEmpty()) intentFilter.addAll(allIntentFilter.map { it.key })

                allIntentFilter.forEach { (type, intent) ->
                    packageUtils.queryIntentActivities(intent, PackageManager.MATCH_ALL).onEach {
                        existIntentApps.add(it.activityInfo.packageName)
                        val info = AppIntentInfo(
                            it.loadLabel(packageManager).toString(), it.activityInfo.packageName,
                            intent.action!!, it.activityInfo.name, type
                        )
                        allIntentInfos.add(info)
                        allResolveInfoMap[info] = it
                    }
                }

                val enabledApps = context.getStringSet(IntentPrefs, enabledListKey, ArraySet())

                val sortList = ArrayList<AppInfo>()
                existIntentApps.forEachIndexed { _, packName ->
                    val info = packageUtils.getInstalledAppInfo(packName, 0)
                        ?: return@forEachIndexed

                    if (!showSystemApps && info.isSystem) {
                        allIntentInfos.removeIf { it.packName == packName }
                        return@forEachIndexed
                    }

                    if (enabledApps.contains(packName)) sortList.add(info)
                    else allAppInfos.add(info)

                    context.getStringSet(IntentPrefs, packName, ArraySet()).apply {
                        forEachIndexed { _, js ->
                            val info = safeOfNull { Json.decodeFromString<AppIntentInfo>(js) }
                                ?: return@forEachIndexed
                            val action = info.action
                            val type = info.type
                            val activity = info.activity
                            val find = allIntentInfos.find {
                                it.action == action && it.type == type && it.packName == packName
                                        && it.activity == activity
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

                filterAppInfos = allAppInfos
            }

            binding.recyclerView.adapter?.notifyDataSetChangedIgnore()

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
        packName: String, appInfos: List<AppIntentInfo>,
        appEnabled: List<AppIntentInfo>, types: Array<IntentType>, position: Int
    ) {
        val appResolveInfos = allResolveInfoMap.filterKeys { it.packName == packName }
        IntentInfoSelectDialog(
            requireActivity(), true, appInfos, appResolveInfos
        ).apply {
            setEnabledList(ArrayList(appEnabled))
            setSelectAllMode(true)
            setOnSelectIntentInfoListener(object : OnSelectIntentInfoListener {
                override fun resultSelectIntentInfos(list: ArrayList<AppIntentInfo>) {
                    saveAppIntentList(packName, list, *types)
                    saveEnabledAppList(packName, list)
                    if (position >= 0) {
                        binding.recyclerView.adapter?.wrapper?.notifyItemChanged(position)
                    }
                }
            })
            show()
        }
    }

    private fun selectAllInfos(vararg type: IntentType) {
        val filte = getIntentFilter(*type)
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
            binding.recyclerView.adapter?.notifyDataSetChangedIgnore()
        }
    }

    fun saveAppIntentList(
        packName: String, list: ArrayList<AppIntentInfo>, vararg types: IntentType
    ) {
        val context = requireActivity()
        val filte = getIntentFilter(*types)
        val appIntents = ArrayList<AppIntentInfo>().apply {
            context.getStringSet(IntentPrefs, packName, ArraySet()).forEachIndexed { _, js ->
                val info = safeOfNull { Json.decodeFromString<AppIntentInfo>(js) }
                    ?: return@forEachIndexed
                add(info)
            }
        }
        appIntents.removeIf(filte)
        if (list.isNotEmpty()) appIntents.addAll(list)

        allEnabledInfos.removeIf { it.packName == packName }
        allEnabledInfos.addAll(appIntents)
        if (appIntents.isNotEmpty()) setIntentTypeFilter()

        if (appIntents.isEmpty()) {
            context.removeKey(IntentPrefs, packName)
        } else {
            val list = appIntents.mapNotNull {
                safeOfNull { Json.encodeToString(it) }
            }
            context.putStringSet(IntentPrefs, packName, list.toSet())
        }

        context.sendPrefsValue(
            "android", "custom_config_app_intent_list_update_app_config", packName
        )
    }

    fun saveEnabledAppList(packName: String, list: ArrayList<AppIntentInfo>) {
        val context = requireActivity()
        val enabledApps = context.getStringSet(IntentPrefs, enabledListKey, ArraySet())
        val intents = context.getStringSet(IntentPrefs, packName, ArraySet())
        val isAdd = list.isNotEmpty() || intents.isNotEmpty()
        val newList = ArraySet(enabledApps).apply {
            remove(packName)
            if (isAdd) add(packName)
        }
        context.putStringSet(IntentPrefs, enabledListKey, newList.toSet())
        context.sendPrefsValue(
            "android", "custom_config_app_intent_list_update_apps",
            Pair(packName, isAdd)
        )
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
            10 -> MaterialAlertDialogBuilder(requireActivity()).apply {
                setNeutralButton(android.R.string.cancel, null)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    context.clearPrefs(IntentPrefs)
                    findNavController().navigateUp()
                }
                show()
            }
        }
        return true
    }
}