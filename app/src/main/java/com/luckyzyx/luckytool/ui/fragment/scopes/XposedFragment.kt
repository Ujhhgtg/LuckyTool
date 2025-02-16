package com.luckyzyx.luckytool.ui.fragment.scopes

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.preference.Preference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.utils.scopeLife
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.FragmentItem
import com.luckyzyx.luckytool.data.PrefsItem
import com.luckyzyx.luckytool.databinding.DialogScopeVersionInfoBinding
import com.luckyzyx.luckytool.databinding.DialogSearchResultLayoutBinding
import com.luckyzyx.luckytool.databinding.LayoutSearchResultItemBinding
import com.luckyzyx.luckytool.listener.OnSelectSearchResultListener
import com.luckyzyx.luckytool.ui.fragment.base.BaseScopePreferenceFeagment
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusBattery
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusBeaconLink
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusBreenoTouch
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusBrowser
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusCalendar
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusCamera
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusCloudService
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusEyeProtect
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusGallery
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusGames
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusGesture
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusMMS
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusMarket
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusOTA
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusPhoneManager
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusPictorial
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusScreenshot
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusSearchBox
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusSettings
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusSmartSidebar
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusSoundRecorder
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusTeleService
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusThemeStore
import com.luckyzyx.luckytool.ui.fragment.scopes.apps.OplusWeather
import com.luckyzyx.luckytool.ui.fragment.scopes.others.ADM
import com.luckyzyx.luckytool.ui.fragment.scopes.others.AlphaBackupPro
import com.luckyzyx.luckytool.ui.fragment.scopes.others.KsWeb
import com.luckyzyx.luckytool.ui.fragment.scopes.related.AndroidRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.AodRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.ApplicationRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.CorePatch
import com.luckyzyx.luckytool.ui.fragment.scopes.related.DialogRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.FingerPrintRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.LauncherRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.LockScreenRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.Miscellaneous
import com.luckyzyx.luckytool.ui.fragment.scopes.related.SoundRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.related.StatusBarRelated
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarBattery
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarClock
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarControlCenter
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarIcon
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarLayout
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarNetWorkSpeed
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarNotify
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarNotifyRemoval
import com.luckyzyx.luckytool.ui.fragment.scopes.statusbar.StatusBarTiles
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.RestartMenuUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.formatStringAuto
import com.luckyzyx.luckytool.utils.navigatePage
import com.luckyzyx.luckytool.utils.safeOf
import com.luckyzyx.luckytool.utils.setupMenuProvider
import com.luckyzyx.luckytool.utils.showBottomSheet
import com.luckyzyx.luckytool.utils.showToast
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.util.Arrays

@Obfuscate
class XposedFragment : BaseScopePreferenceFeagment(), MenuProvider {

    override val currentPrefsName: String = ModulePrefs

    override val navigateFragmentId: Int = -1

    private val allFragmentItem = ArrayList<FragmentItem>()

    private var loadDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenuProvider(this)
    }

    override fun Context.loadRootPreference(): Preference {
        return Preference(this)
    }

    override fun Context.loadPreferences(): ArrayList<Preference> {
        val allPrefs = ArrayList<Preference>()

        //Android
        addFragmentPreference(this, allPrefs, AndroidRelated())
        //StatusBar
        addFragmentPreference(this, allPrefs, StatusBarRelated())
        //Launcher
        addFragmentPreference(this, allPrefs, LauncherRelated())
        //Aod
        addFragmentPreference(this, allPrefs, AodRelated())
        //LockScreen
        addFragmentPreference(this, allPrefs, LockScreenRelated())
        //Application
        addFragmentPreference(this, allPrefs, ApplicationRelated())
        //Miscellaneous
        addFragmentPreference(this, allPrefs, Miscellaneous())
        //Screenshot
        addFragmentPreference(this, allPrefs, OplusScreenshot())
        //Battery
        addFragmentPreference(this, allPrefs, OplusBattery())
        //Settings
        addFragmentPreference(this, allPrefs, OplusSettings())
        //TeleService
        addFragmentPreference(this, allPrefs, OplusTeleService())
        //Mms
        addFragmentPreference(this, allPrefs, OplusMMS())
        //Browser
        addFragmentPreference(this, allPrefs, OplusBrowser())
        //Camera
        addFragmentPreference(this, allPrefs, OplusCamera())
        //Gallery
        addFragmentPreference(this, allPrefs, OplusGallery())
        //Games
        addFragmentPreference(this, allPrefs, OplusGames())
        //Theme
        addFragmentPreference(this, allPrefs, OplusThemeStore())
        //Market
        addFragmentPreference(this, allPrefs, OplusMarket())
        //CloudService
        addFragmentPreference(this, allPrefs, OplusCloudService())
        //OTA
        addFragmentPreference(this, allPrefs, OplusOTA())
        //Pictorial
        addFragmentPreference(this, allPrefs, OplusPictorial())
        //Gesture
        addFragmentPreference(this, allPrefs, OplusGesture())
        //Directui
        addFragmentPreference(this, allPrefs, OplusBreenoTouch())
        //QuickSearchBox
        addFragmentPreference(this, allPrefs, OplusSearchBox())
        //Weather
        addFragmentPreference(this, allPrefs, OplusWeather())
        //Calendar
        addFragmentPreference(this, allPrefs, OplusCalendar())
        //SmartSidebar
        addFragmentPreference(this, allPrefs, OplusSmartSidebar())
        //PhoneManager
        addFragmentPreference(this, allPrefs, OplusPhoneManager())
        //SoundRecorder
        addFragmentPreference(this, allPrefs, OplusSoundRecorder())
        //EyeProtect
        addFragmentPreference(this, allPrefs, OplusEyeProtect())
        //BeaconLink
        addFragmentPreference(this, allPrefs, OplusBeaconLink())

        //Other App
        addFragmentPreference(this, allPrefs, AlphaBackupPro())
        addFragmentPreference(this, allPrefs, KsWeb())
        addFragmentPreference(this, allPrefs, ADM())

        return allPrefs
    }

    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        if (loadDialog == null) loadDialog =
            MaterialAlertDialogBuilder(requireActivity(), dialogCentered).apply {
                setTitle(getString(R.string.common_words_loading))
                setView(LinearLayout(context).apply {
                    addView(LinearProgressIndicator(context).apply {
                        layoutParams =
                            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                        setPadding(20.dp)
                        isIndeterminate = true
                    })
                })
            }.create()
    }

    override fun onResume() {
        super.onResume()
        val count = safeOf(0) {
            if (preferenceScreen != null) preferenceScreen.preferenceCount else 0
        }
        if (count <= 0) init()
    }

    private fun initAllScopePreferences() {
        allFragmentItem.clear()

        addFragmentItem(requireActivity(), allFragmentItem, StatusBarRelated())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarBattery())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarClock())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarControlCenter())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarIcon())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarLayout())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarNetWorkSpeed())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarNotify())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarNotifyRemoval())
        addFragmentItem(requireActivity(), allFragmentItem, StatusBarTiles())

        addFragmentItem(requireActivity(), allFragmentItem, AndroidRelated())
        addFragmentItem(requireActivity(), allFragmentItem, CorePatch())
        addFragmentItem(requireActivity(), allFragmentItem, AodRelated())
        addFragmentItem(requireActivity(), allFragmentItem, ApplicationRelated())
        addFragmentItem(requireActivity(), allFragmentItem, DialogRelated())
        addFragmentItem(requireActivity(), allFragmentItem, FingerPrintRelated())
        addFragmentItem(requireActivity(), allFragmentItem, LauncherRelated())
        addFragmentItem(requireActivity(), allFragmentItem, LockScreenRelated())
        addFragmentItem(requireActivity(), allFragmentItem, Miscellaneous())

        addFragmentItem(requireActivity(), allFragmentItem, OplusBattery())
        addFragmentItem(requireActivity(), allFragmentItem, OplusBreenoTouch())
        addFragmentItem(requireActivity(), allFragmentItem, OplusBrowser())
        addFragmentItem(requireActivity(), allFragmentItem, OplusCalendar())
        addFragmentItem(requireActivity(), allFragmentItem, OplusCamera())
        addFragmentItem(requireActivity(), allFragmentItem, OplusCloudService())
        addFragmentItem(requireActivity(), allFragmentItem, OplusEyeProtect())
        addFragmentItem(requireActivity(), allFragmentItem, OplusGallery())
        addFragmentItem(requireActivity(), allFragmentItem, OplusGames())
        addFragmentItem(requireActivity(), allFragmentItem, OplusGesture())
        addFragmentItem(requireActivity(), allFragmentItem, OplusMarket())
        addFragmentItem(requireActivity(), allFragmentItem, OplusMMS())
        addFragmentItem(requireActivity(), allFragmentItem, OplusOTA())
        addFragmentItem(requireActivity(), allFragmentItem, OplusPhoneManager())
        addFragmentItem(requireActivity(), allFragmentItem, OplusPictorial())
        addFragmentItem(requireActivity(), allFragmentItem, OplusScreenshot())
        addFragmentItem(requireActivity(), allFragmentItem, OplusSearchBox())
        addFragmentItem(requireActivity(), allFragmentItem, OplusSettings())
        addFragmentItem(requireActivity(), allFragmentItem, OplusSmartSidebar())
        addFragmentItem(requireActivity(), allFragmentItem, OplusSoundRecorder())
        addFragmentItem(requireActivity(), allFragmentItem, OplusTeleService())
        addFragmentItem(requireActivity(), allFragmentItem, OplusWeather())
        addFragmentItem(requireActivity(), allFragmentItem, SoundRelated())
        addFragmentItem(requireActivity(), allFragmentItem, OplusThemeStore())
        addFragmentItem(requireActivity(), allFragmentItem, OplusBeaconLink())

        addFragmentItem(requireActivity(), allFragmentItem, ADM())
        addFragmentItem(requireActivity(), allFragmentItem, AlphaBackupPro())
        addFragmentItem(requireActivity(), allFragmentItem, KsWeb())

    }

    private fun addFragmentPreference(
        context: Context,
        list: ArrayList<Preference>,
        fragment: BaseScopePreferenceFeagment
    ) {
        list.add(fragment.getRootPreference(context))
    }

    private fun addFragmentItem(
        context: Context,
        list: ArrayList<FragmentItem>,
        fragment: BaseScopePreferenceFeagment
    ) {
        if (fragment.navigateFragmentId == -1) return
        list.add(
            FragmentItem(
                fragment, fragment.navigateFragmentId,
                fragment.readPrefsItem(context)
            )
        )
    }

    private fun init() {
        loadDialog?.show()
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            requireActivity().loadPreferences().forEachIndexed { index, preference ->
                try {
                    addPreference(preference)
                } catch (_: Throwable) {
                    context.showToast("Error: $index ${preference.key}")
                    return@forEachIndexed
                }
            }
        }
        loadDialog?.dismiss()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.menu_search)).apply {
            setIcon(R.drawable.ic_baseline_search_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 2, 0, getString(R.string.menu_reboot)).apply {
            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 3, 0, getString(R.string.menu_versioninfo)).apply {
            setIcon(R.drawable.ic_baseline_extension_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            1 -> requireActivity().showSearchDialog()
            2 -> RestartMenuUtils.showMainRestartMenu(requireActivity())
            3 -> requireActivity().showBottomDialog()
        }
        return true
    }

    private fun Context.showBottomDialog() {
        scopeLife {
            val dialogBinding = DialogScopeVersionInfoBinding.inflate(layoutInflater)
            val markwon = Markwon.builder(this@showBottomDialog).apply {
                usePlugin(TablePlugin.create(this@showBottomDialog))
            }.build()
            showBottomSheet(dialogBinding.root)
            val list = ArrayList<String>().apply {
                add("| name | package | version |")
                add("| ------ | ------ | ------|")
            }
            val xposedScope = resources.getStringArray(R.array.xposed_scope)
            Arrays.sort(xposedScope)
            xposedScope.forEach {
                val appVerInfo = AppUtils(this@showBottomDialog).getAppVerInfo(it)
                    ?: return@forEach
                list.add("| ${appVerInfo.appName} | $it |  ${appVerInfo.versionName}(${appVerInfo.versionCode})[${appVerInfo.versionCommit}] |")
            }
            markwon.setMarkdown(dialogBinding.tv, formatStringAuto(list, "\n"))
        }
    }

    private fun Context.showSearchDialog() {
        scopeLife {
            initAllScopePreferences()

            val binding = DialogSearchResultLayoutBinding.inflate(layoutInflater)

            val dialog = MaterialAlertDialogBuilder(this@showSearchDialog).apply {
                setView(binding.root)
            }.show() ?: return@scopeLife

            val searchResultAdapter = SearchResultAdapter(
                allFragmentItem, object : OnSelectSearchResultListener {
                    override fun resultItem(fragmentItem: FragmentItem, prefsItem: PrefsItem) {
                        dialog.dismiss()
                        if (prefsItem.fragmentId == -1) return
                        prefsItem.fragmentId?.let { _ ->
                            val bundle = Bundle().apply {
                                putCharSequence("title_text", prefsItem.fragmentTitle)
                                putString("scrollKey", prefsItem.key)
                                putInt("scrollPosition", prefsItem.position)
                            }
                            navigatePage(prefsItem.fragmentId, bundle)
                        }
                    }
                })

            binding.searchViewLayout.apply {
                hint = "Title / Summary"
            }

            binding.searchView.apply {
                addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                    searchResultAdapter.getFilter.filter(text)
                })
            }

            binding.recyclerView.apply {
                adapter = searchResultAdapter
                layoutManager = LinearLayoutManager(context)
                FastScrollerBuilder(this).useMd2Style().build()
            }
        }
    }

    @Obfuscate
    class SearchResultAdapter(
        allFragmentItem: ArrayList<FragmentItem>,
        private val onSelectSearchResultListener: OnSelectSearchResultListener
    ) : RecyclerView.Adapter<SearchResultAdapter.SearchResultItemHolder>() {

        private var allFragmentItemDatas = ArrayMap<Int, FragmentItem>()
        private var allPrefsItemDatas = ArrayMap<Int, ArrayList<PrefsItem>>()

        private var allDatas = ArrayList<PrefsItem>()
        private var filterDatas = ArrayList<PrefsItem>()

        init {
            allFragmentItemDatas.clear()
            allPrefsItemDatas.clear()
            allDatas.clear()
            filterDatas.clear()

            allFragmentItem.forEachIndexed { _, fragmentItem ->
                allFragmentItemDatas[fragmentItem.fragmentId] = fragmentItem
                allPrefsItemDatas[fragmentItem.fragmentId] = fragmentItem.allPrefsItem
                allDatas.addAll(fragmentItem.allPrefsItem)
            }
//            LogUtils.d("SearchResultAdapter", "init", allDatas.size.toString(), true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemHolder {
            val binding = LayoutSearchResultItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SearchResultItemHolder(binding)
        }

        override fun onBindViewHolder(holder: SearchResultItemHolder, position: Int) {
            val prefsItem = filterDatas[position]
            val icon = prefsItem.icon
            val title = prefsItem.title
            val summary = prefsItem.summary
            val fragmentId = prefsItem.fragmentId
            val fragmentItem = allFragmentItemDatas[fragmentId]!!

            holder.item.setOnClickListener(null)
            holder.itemIcon.setImageDrawable(null)
            holder.itemTitle.text = null
            holder.itemSummary.text = null

            holder.item.setOnClickListener {
                onSelectSearchResultListener.resultItem(fragmentItem, prefsItem)
            }
            if (icon == null) holder.itemIcon.isVisible = false
            else holder.itemIcon.setImageDrawable(icon)
            holder.itemTitle.text = title
            if (summary.isNullOrBlank()) holder.itemSummary.isVisible = false
            else holder.itemSummary.text = summary
        }

        override fun getItemCount(): Int = filterDatas.size

        val getFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                filterDatas = if (constraint.isNotBlank()) {
                    ArrayList(allDatas.filter {
                        it.title?.contains(constraint, true) == true &&
                                it.isVisible == true && it.fragmentId != -1
                    })
                } else arrayListOf()
//                LogUtils.d("SearchResultAdapter", "getFilter", filterDatas.size.toString(), true)
                val filterResults = FilterResults()
                filterResults.values = filterDatas
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filterDatas = results.values as ArrayList<PrefsItem>
                refreshDatas()
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
        }

        @Obfuscate
        class SearchResultItemHolder(binding: LayoutSearchResultItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val item: ConstraintLayout = binding.root
            val itemIcon: ImageView = binding.itemIcon
            val itemTitle: TextView = binding.itemTitle
            val itemSummary: TextView = binding.itemSummary
        }
    }
}