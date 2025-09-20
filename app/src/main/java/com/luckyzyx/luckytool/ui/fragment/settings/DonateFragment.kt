package com.luckyzyx.luckytool.ui.fragment.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.drake.net.Get
import com.drake.net.utils.scopeLife
import com.drake.net.utils.scopeNetLife
import com.google.android.material.chip.Chip
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.data.DonateDetailInfo
import com.luckyzyx.luckytool.data.DonateInfo
import com.luckyzyx.luckytool.databinding.FragmentDonateListBinding
import com.luckyzyx.luckytool.selector.SortFilterBottomSheetDialog
import com.luckyzyx.luckytool.ui.fragment.base.BaseFragment
import com.luckyzyx.luckytool.utils.AESCrypt
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.formatStringAuto
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putString
import com.luckyzyx.luckytool.utils.safeOfNull
import com.luckyzyx.luckytool.utils.showToast
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import org.json.JSONArray
import org.json.JSONObject
import org.lsposed.lsparanoid.Obfuscate
import java.io.File
import java.text.DecimalFormat

@Obfuscate
class DonateFragment : BaseFragment<FragmentDonateListBinding>() {

    private lateinit var donateDataTempFile: File
    private lateinit var donateDataFile: File

    private val showDetailedKey = "show_detailed_donate_data"
    private val showOtherCurrencyKey = "show_other_currency_donate_data"

    private var filterString: CharSequence = ""

    private lateinit var sortFilterBottomSheetDialog: SortFilterBottomSheetDialog
    private var isReverse = false
    private var sortMode = 0
    private var showDetail = false
    private var otherCurrency = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        showDetail = requireActivity().getBoolean(SettingsPrefs, showDetailedKey, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener { initData(context) }
            isRefreshing = true
        }
        binding.searchView.apply {
            isEnabled = false
            text = null
            addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                filterString = text ?: ""
                loadJson(context, donateDataFile)
            })
        }
        initSortFilterSelector()
        binding.searchViewLayout.apply {
            hint = "Name"
            setEndIconOnClickListener {
                sortFilterBottomSheetDialog.show()
            }
        }

        initData(requireActivity())
    }

    private fun initData(context: Context) {
        scopeLife {
            donateDataTempFile = File(requireActivity().cacheDir, "dTemp")
            donateDataFile = File(requireActivity().filesDir, "dd")
            if (donateDataTempFile.exists()) donateDataTempFile.delete()
            if (donateDataFile.exists()) checkDonateData(context)
            else downloadJson(context, formatDate("YYYYMMddHHmm"))
        }
    }

    private fun initSortFilterSelector() {
        val sorts = arrayListOf(
            getString(R.string.donate_info_time),
            getString(R.string.donate_info_money),
            getString(R.string.donate_info_order)
        )
        if (showDetail) sorts.removeLastOrNull()
        sortFilterBottomSheetDialog = SortFilterBottomSheetDialog(requireActivity()).apply {
            setReverse(true) { _, isChecked ->
                isReverse = isChecked
                loadJson(context, donateDataFile)
            }
            setSortChips(true, sorts.toTypedArray(), sortMode) { _, checkedIds ->
                sortMode = checkedIds.firstOrNull() ?: 0
                loadJson(context, donateDataFile)
            }
            setFilterChips(
                true, arrayOf(
                    Chip(context).apply {
                        text = getString(R.string.donate_detailed_data)
                        isCheckable = true
                        isClickable = true
                        isChecked = showDetail
                        setOnCheckedChangeListener { buttonView, isChecked ->
                            if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                            showDetail = isChecked
                            context.putBoolean(SettingsPrefs, showDetailedKey, showDetail)
                            dismiss()
                            initSortFilterSelector()
                            show()
                            loadJson(context, donateDataFile)
                        }
                    }, Chip(context).apply {
                        text = getString(R.string.donate_other_currency)
                        isCheckable = true
                        isClickable = true
                        isChecked = otherCurrency
                        setOnCheckedChangeListener { buttonView, isChecked ->
                            if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                            otherCurrency = isChecked
                            context.putBoolean(SettingsPrefs, showOtherCurrencyKey, otherCurrency)
                            loadJson(context, donateDataFile)
                        }
                    })
            )
        }
    }

    private fun checkDonateData(context: Context) {
        scopeNetLife {
            val donateDataUrl =
                "https://api.github.com/repos/LuckyOSTeam/LuckyOSTeam.github.io/releases/tags/luckytool_donates"
            val lastUpdateDate = context.getString(SettingsPrefs, "last_update_dd_date", "null")
            val getJson = Get<String>(donateDataUrl).await()
            JSONObject(getJson).apply {
                val date = optString("name", "")
                if (date.isNullOrBlank()) return@apply
                if (date != lastUpdateDate) downloadJson(context, date)
                else loadJson(context, donateDataFile)
            }
        }.catch {
            context.showToast("Exception while checking data!")
            LogUtils.e("checkDonateData", "checking", it.toString(), true)
            return@catch
        }
    }

    private fun downloadJson(context: Context, date: String) {
        scopeNetLife {
            val file =
                Get<File>("https://raw.gitmirror.com/LuckyOSTeam/LuckyOSTeam.github.io/main/LuckyTool/donate.json") {
                    setDownloadDir(donateDataTempFile)
                    setDownloadMd5Verify()
                    setDownloadTempFile()
                }.await()
            if (file.exists()) encryptFile(context, file, date)
        }.catch {
            context.showToast("Exception while download data!")
            LogUtils.e("downloadJson", "download", it.toString(), true)
            return@catch
        }
    }

    private fun encryptFile(context: Context, file: File, date: String) {
        scopeLife {
            val jsonEncrypt = AESCrypt.encrypt(file.readText())
            donateDataFile.writeText(jsonEncrypt)
            file.delete()
            context.putString(SettingsPrefs, "last_update_dd_date", date)
            loadJson(context, donateDataFile)
        }
    }

    private fun loadJson(context: Context, file: File) {
        scopeLife {
            val jsonObject = safeOfNull {
                val jsonDecrypt = AESCrypt.decrypt(file.readText())
                JSONObject(jsonDecrypt)
            }
            if (jsonObject == null) {
                file.delete()
                context.showToast(getString(R.string.donate_data_decode_error))
                return@scopeLife
            }

            val markdownList = ArrayList<String>()
            val datas = jsonObject.optJSONArray("datas") ?: JSONArray()
            if (showDetail.not()) formatUserInfo(context, datas, markdownList)
            else formatUserDetailInfo(context, datas, markdownList)

            val markwon = Markwon.builder(context).apply {
                usePlugin(TablePlugin.create(context))
            }.build()
            binding.tv.apply {
                markwon.setMarkdown(this, formatStringAuto(markdownList, "\n"))
            }
            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchView.isEnabled = true
        }
    }

    @Suppress("UNUSED_VARIABLE")
    private fun formatUserInfo(
        context: Context, jsonArray: JSONArray, markdownList: ArrayList<String>
    ) {
        markdownList.apply {
            add("| Name | Money |")
            add("| :------: | :------: |")
        }

        var totalDetailCount = 0
        var totalRmbCount = 0.0
        var totalOtherDetailCount = 0
        var totalOtherCount = 0.0

        val donateList = ArrayList<DonateInfo>()
        for (i in 0 until jsonArray.length()) {
            var userDetailCount = 0
            var userRmbCount = 0.0
            var userOtherDetailCount = 0
            var userOtherCount = 0.0

            val userObject = jsonArray.optJSONObject(i) ?: continue
            val name = userObject.optString("name")

            val details = userObject.optJSONArray("details") ?: continue
            for (o in 0 until details.length()) {
                val info = details.optJSONObject(o) ?: continue

                val time = info.optString("time")
                val channel = info.optString("channel")
                val money = info.optDouble("money", 0.0)
                val order = info.optString("order")
                val unit = info.optString("unit")

                when (unit) {
                    "RMB" -> {
                        userDetailCount++
                        userRmbCount += money
                    }

                    "$" -> {
                        userOtherDetailCount++
                        userOtherCount += money
                    }
                }
            }

            totalDetailCount += userDetailCount
            totalRmbCount += userRmbCount
            totalOtherDetailCount += userOtherDetailCount
            totalOtherCount += userOtherCount

            if (!otherCurrency && userRmbCount > 0) donateList.add(
                DonateInfo(name, userRmbCount, userDetailCount)
            )
            if (otherCurrency && userOtherCount > 0) donateList.add(
                DonateInfo(name, userOtherCount, userOtherDetailCount, "$")
            )
        }

        val develop = context.getBoolean(SettingsPrefs, "hidden_function", false)
        donateList.apply {
            when (sortMode) {
                1 -> sortBy { it.money }
                2 -> sortBy { it.details }
            }
            if (isReverse) reverse()

            if (donateList.isNotEmpty() && develop) {
                val formatRmb = DecimalFormat("0.00").format(totalRmbCount).toDouble()
                val formatOth = DecimalFormat("0.00").format(totalOtherCount).toDouble()

                if (formatRmb > 0) donateList.add(
                    0, DonateInfo("develop", formatRmb, totalDetailCount)
                )
                if (formatOth > 0) donateList.add(
                    1, DonateInfo("develop", formatOth, totalOtherDetailCount, "$")
                )
            }

            forEachIndexed { _, info ->
                if (filterString.isBlank() || info.name.contains(filterString, true)) {
                    markdownList.add("| ${info.name} | ${info.money} ${info.unit} |")
                }
            }
        }
    }

    private fun formatUserDetailInfo(
        context: Context, jsonArray: JSONArray, markdownList: ArrayList<String>
    ) {
        markdownList.apply {
            add("| Name | Time | Money | Channel |")
            add("| :------: | :------: | :------: | :------: |")
        }

        var totalDetailCount = 0
        var totalRmbCount = 0.0
        var totalOtherDetailCount = 0
        var totalOtherCount = 0.0

        val donateList = ArrayList<DonateDetailInfo>()
        for (i in 0 until jsonArray.length()) {
            var userDetailCount = 0
            var userRmbCount = 0.0
            var userOtherDetailCount = 0
            var userOtherCount = 0.0

            val userObject = jsonArray.optJSONObject(i) ?: continue
            val name = userObject.optString("name")

            val details = userObject.optJSONArray("details") ?: continue
            for (o in 0 until details.length()) {
                val info = details.optJSONObject(o) ?: continue

                val time = info.optString("time")
                val channel = info.optString("channel")
                val money = info.optDouble("money", 0.0)
                val order = info.optString("order")
                val unit = info.optString("unit")

                when (unit) {
                    "RMB" -> {
                        userDetailCount++
                        userRmbCount += money
                        if (otherCurrency) continue
                    }

                    "$" -> {
                        userOtherDetailCount++
                        userOtherCount += money
                        if (!otherCurrency) continue
                    }
                }
                if (money > 0) {
                    donateList.add(DonateDetailInfo(name, time, channel, money, order, unit))
                }
            }

            totalDetailCount += userDetailCount
            totalRmbCount += userRmbCount
            totalOtherDetailCount += userOtherDetailCount
            totalOtherCount += userOtherCount
        }

        val develop = context.getBoolean(SettingsPrefs, "hidden_function", false)
        donateList.apply {
            when (sortMode) {
                1 -> sortBy { it.money }
            }
            if (isReverse) reverse()

            if (donateList.isNotEmpty() && develop) {
                val formatRmb = DecimalFormat("0.00").format(totalRmbCount).toDouble()
                val formatOth = DecimalFormat("0.00").format(totalOtherCount).toDouble()

                if (formatRmb > 0) donateList.add(
                    0, DonateDetailInfo("develop", "all", "all", formatRmb, "null")
                )
                if (formatOth > 0) donateList.add(
                    1, DonateDetailInfo("develop", "all", "all", formatOth, "null", "$")
                )
            }

            forEachIndexed { _, info ->
                if (filterString.isBlank() || info.name.contains(filterString)) {
                    markdownList.add("| ${info.name} | ${info.time} | ${info.money} ${info.unit} | ${info.channel} |")
                }
            }
        }
    }
}
