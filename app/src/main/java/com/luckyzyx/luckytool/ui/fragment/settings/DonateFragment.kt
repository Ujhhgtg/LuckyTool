package com.luckyzyx.luckytool.ui.fragment.settings

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.Get
import com.drake.net.utils.scopeLife
import com.drake.net.utils.scopeNetLife
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.databinding.FragmentDonateListBinding
import com.luckyzyx.luckytool.databinding.LayoutDonateItemBinding
import com.luckyzyx.luckytool.utils.DCInfo
import com.luckyzyx.luckytool.utils.DInfo
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.base64Decode
import com.luckyzyx.luckytool.utils.base64Encode
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.putString
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.DecimalFormat

@Obfuscate
class DonateFragment : Fragment() {

    private lateinit var binding: FragmentDonateListBinding
    private lateinit var ddFile: File
    private var donateAdapter: DonateListAdapter? = null
    private val allData = ArrayList<DInfo>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonateListBinding.inflate(inflater)
        return binding.root
    }

    fun init(context: Context) {
        scopeLife {
            binding.searchViewLayout.apply {
                hint = "Name"
                isHintEnabled = true
                isHintAnimationEnabled = true
            }
            binding.searchView.apply {
                isEnabled = false
                text = null
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int
                    ) {
                        donateAdapter?.getFilter?.filter(s.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            }

            binding.swipeRefreshLayout.apply {
                setOnRefreshListener { init(context) }
                isRefreshing = true
            }
            ddFile = File(context.filesDir.path + "/dd")
            if (ddFile.exists()) checkDonateData(context)
            else downloadJson(context, formatDate("YYYYMMddHHmm"))
        }
    }

    private fun checkDonateData(context: Context) {
        scopeNetLife {
            val latestUrl =
                "https://api.github.com/repos/LuckyOSTeam/LuckyOSTeam.github.io/releases/tags/luckytool_donates"
            val lastDDDate = context.getString(SettingsPrefs, "last_update_dd_date", "null")
            val getDoc = Get<String>(latestUrl).await()
            JSONObject(getDoc).apply {
                val date = optString("name").takeIf { e -> e.isNotBlank() } ?: return@scopeNetLife
                if (date != lastDDDate) downloadJson(context, date)
                else loadJson(context, ddFile)
            }
        }.catch { return@catch }
    }

    private fun downloadJson(context: Context, date: String) {
        scopeNetLife {
            val file =
                Get<File>("https://raw.gitmirror.com/LuckyOSTeam/LuckyOSTeam.github.io/main/LuckyTool/donate.json") {
                    setDownloadDir(ddFile)
                    setDownloadMd5Verify()
                    setDownloadTempFile()
                }.await()
            if (file.exists()) {
                loadJson(context, file)
                context.putString(SettingsPrefs, "last_update_dd_date", date)
            }
        }.catch { if (ddFile.exists()) loadJson(context, ddFile) }
    }

    private fun loadJson(context: Context, file: File) {
        scopeLife {
            if (file.readText().contains("datas")) {
                val json = base64Encode(file.readText())
                file.writeText("e$json")
            }
            allData.clear()
            val jsonObject = JSONObject(
                base64Decode(file.readText().let { it.substring(1, it.length) })
            )
            val datas = jsonObject.optJSONArray("datas") ?: JSONArray()
            var count = 0.0
            var chsCount = 0.0
            var otherCount = 0.0
            for (i in 0 until datas.length()) {
                val obj = datas.getJSONObject(i)
                val name = obj.optString("name")
                val details = obj.optJSONArray("details") ?: JSONArray()
                val infos = ArrayList<DCInfo>()
                for (o in 0 until details.length()) {
                    count++
                    val info = details.optJSONObject(o)
                    val time = info.optString("time")
                    val channel = info.optString("channel")
                    val money = info.optDouble("money")
                    val order = info.optString("order")
                    val unit = info.optString("unit")
                    infos.add(DCInfo(time, channel, money, order, unit))
                    when (unit) {
                        "RMB" -> chsCount += money
                        "$" -> otherCount += money
                    }
                }
                allData.add(DInfo(name, infos.toTypedArray()))
            }
            val develop = context.getBoolean(SettingsPrefs, "hidden_function", false)
            if (develop) allData.add(
                0, DInfo(
                    "$count", arrayOf(
                        DCInfo("", "", DecimalFormat("0.00").format(chsCount).toDouble(), ""),
                        DCInfo("", "", DecimalFormat("0.00").format(otherCount).toDouble(), "", "$")
                    )
                )
            )
            donateAdapter = DonateListAdapter(context, allData)
            binding.recyclerView.apply {
                adapter = donateAdapter
                layoutManager = LinearLayoutManager(context)
            }
            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchView.isEnabled = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(requireActivity())
    }

    class DonateListAdapter(val context: Context, data: ArrayList<DInfo>) :
        RecyclerView.Adapter<DonateListAdapter.ViewHolder>() {

        var allDatas = ArrayList<DInfo>()
        var filterDatas = ArrayList<DInfo>()

        init {
            allDatas = data
            filterDatas = data
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                LayoutDonateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = filterDatas[position].name
            filterDatas[position].details.apply {
                var isChs = false
                var isOther = false
                var count = 0.0
                var chsCount = 0.0
                var otherCount = 0.0
                forEach {
                    when (it.unit) {
                        "RMB" -> {
                            isChs = true
                            chsCount += it.money
                        }

                        "$" -> {
                            isOther = true
                            otherCount += it.money
                        }

                        else -> count += it.money
                    }
                }
                val newline = if (isChs && isOther) "\n" else ""
                val final =
                    (if (isChs) "$chsCount RMB" else "") + newline + (if (isOther) "$otherCount $" else "") + (if (count != 0.0) "\n$count" else "")
                holder.money.text = final
            }
        }

        val getFilter = object : Filter() {
                override fun performFiltering(constraint: CharSequence): FilterResults {
                    filterDatas = if (constraint.isBlank()) allDatas
                    else {
                        val filterlist = ArrayList<DInfo>()
                        allDatas.forEach {
                            if (it.name.lowercase().contains(constraint.toString().lowercase())) {
                                filterlist.add(it)
                            }
                        }
                        filterlist
                    }
                    val filterResults = FilterResults()
                    filterResults.values = filterDatas
                    return filterResults
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                    filterDatas = results?.values as ArrayList<DInfo>
                    refreshDatas()
                }
            }

        override fun getItemCount(): Int = filterDatas.size

        @SuppressLint("NotifyDataSetChanged")
        fun refreshDatas() {
            notifyDataSetChanged()
        }

        class ViewHolder(binding: LayoutDonateItemBinding) : RecyclerView.ViewHolder(binding.root) {
            val name = binding.donateName
            val money = binding.donateMoney
        }
    }
}
