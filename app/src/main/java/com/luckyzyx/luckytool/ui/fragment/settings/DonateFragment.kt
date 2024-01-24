package com.luckyzyx.luckytool.ui.fragment.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.drake.net.Get
import com.drake.net.utils.scopeLife
import com.drake.net.utils.scopeNetLife
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.FragmentDonateListBinding
import com.luckyzyx.luckytool.utils.AESCrypt
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.formatStringAuto
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putString
import com.luckyzyx.luckytool.utils.safeOfNull
import com.luckyzyx.luckytool.utils.setupMenuProvider
import com.luckyzyx.luckytool.utils.showToast
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.DecimalFormat

@Obfuscate
class DonateFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentDonateListBinding

    private lateinit var donateDataFile: File

    private var isShowDetailed = false

    private val showDetailedKey = "show_detailed_donate_data"

    private var filterString: CharSequence = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        isShowDetailed = requireActivity().getBoolean(SettingsPrefs, showDetailedKey, false)
        binding = FragmentDonateListBinding.inflate(inflater)
        return binding.root
    }

    fun init(context: Context) {
        scopeLife {
            binding.swipeRefreshLayout.apply {
                setOnRefreshListener { init(context) }
                isRefreshing = true
            }
            binding.searchViewLayout.apply {
                hint = "Name"
                isHintEnabled = true
                isHintAnimationEnabled = true
            }
            binding.searchView.apply {
                isEnabled = false
                text = null
                addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                    filterString = text ?: ""
                    loadJson(context, donateDataFile)
                })
            }

            donateDataFile = File(context.filesDir, "dd")
            if (donateDataFile.exists()) checkDonateData(context)
            else downloadJson(context, formatDate("YYYYMMddHHmm"))
        }
    }

    private fun checkDonateData(context: Context) {
        scopeNetLife {
            val donateDataUrl =
                "https://api.github.com/repos/LuckyOSTeam/LuckyOSTeam.github.io/releases/tags/luckytool_donates"
            val lastUpdateDate = context.getString(SettingsPrefs, "last_update_dd_date", "null")
            val getDoc = Get<String>(donateDataUrl).await()
            JSONObject(getDoc).apply {
                val date = optString("name").takeIf { e -> e.isNotBlank() } ?: return@scopeNetLife
                if (date != lastUpdateDate) downloadJson(context, date)
                else loadJson(context, donateDataFile)
            }
        }.catch { return@catch }
    }

    private fun downloadJson(context: Context, date: String) {
        scopeNetLife {
            val file =
                Get<File>("https://raw.gitmirror.com/LuckyOSTeam/LuckyOSTeam.github.io/main/LuckyTool/donate.json") {
                    setDownloadDir(donateDataFile)
                    setDownloadMd5Verify()
                    setDownloadTempFile()
                }.await()
            if (file.exists()) {
                initJsonFile(context, file)
                context.putString(SettingsPrefs, "last_update_dd_date", date)
            }
        }.catch { if (donateDataFile.exists()) loadJson(context, donateDataFile) }
    }

    private fun initJsonFile(context: Context, file: File) {
        scopeLife {
            if (file.readText().contains("datas")) {
                val jsonEncrypt = AESCrypt.encrypt(file.readText())
                file.writeText(jsonEncrypt)
            } else loadJson(context, file)
        }
    }

    private fun loadJson(context: Context, file: File) {
        scopeLife {
            if (file.readText().contains("datas")) initJsonFile(context, file)
            val jsonObject = safeOfNull {
                val jsonDecrypt = AESCrypt.decrypt(file.readText())
                JSONObject(jsonDecrypt)
            }
            if (jsonObject == null) {
                file.delete()
                context.showToast(getString(R.string.donate_data_decode_error))
                return@scopeLife
            }

            val list = ArrayList<String>().apply {
                if (isShowDetailed) {
                    add("| Name | Time | Money | Channel |")
                    add("| :------: | :------: | :------: | :------: |")
                } else {
                    add("| Name | Money |")
                    add("| :------: | :------: |")
                }
            }
            val datas = jsonObject.optJSONArray("datas") ?: JSONArray()

            var detailCount = 0
            var rmbCount = 0.0
            var otherCount = 0.0

            for (i in 0 until datas.length()) {
                var dCount = 0
                var rCount = 0.0
                var oCount = 0.0

                val obj = datas.optJSONObject(i) ?: continue
                val name = obj.optString("name")
                if (filterString.isNotBlank() && name.contains(filterString).not()) continue
                
                val details = obj.optJSONArray("details") ?: continue
                for (o in 0 until details.length()) {
                    val info = details.optJSONObject(o) ?: continue
                    dCount++

                    val time = info.optString("time")
                    val channel = info.optString("channel")
                    val money = info.optDouble("money")

                    @Suppress("UNUSED_VARIABLE")
                    val order = info.optString("order")

                    @Suppress("MoveVariableDeclarationIntoWhen")
                    val unit = info.optString("unit")

                    when (unit) {
                        "RMB" -> rCount += money
                        "$" -> oCount += money
                    }
                    if (isShowDetailed) list.add("| $name | $time | $money | $channel |")
                }
                val moneyStr = when {
                    rCount != 0.0 && oCount != 0.0 -> "$rCount RMB & $oCount $"
                    rCount != 0.0 -> "$rCount RMB"
                    oCount != 0.0 -> "$oCount $"
                    else -> ""
                }
                detailCount += dCount
                rmbCount += rCount
                otherCount += oCount
                if (isShowDetailed.not()) list.add("| $name | $moneyStr |")
            }

            val develop = context.getBoolean(SettingsPrefs, "hidden_function", false)
            if (develop) {
                val formatRmb = DecimalFormat("0.00").format(rmbCount).toDouble()
                val formatOth = DecimalFormat("0.00").format(otherCount).toDouble()
                list.add(
                    2, if (isShowDetailed) "| develop | now | $formatRmb RMB & $formatOth $ | all |"
                    else "| develop | $formatRmb RMB & $formatOth $ |"
                )
            }

            val markwon = Markwon.builder(context).apply {
                usePlugin(TablePlugin.create(context))
            }.build()
            binding.tv.apply {
                markwon.setMarkdown(this, formatStringAuto(list, "\n"))
            }
            binding.swipeRefreshLayout.isRefreshing = false
            binding.searchView.isEnabled = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(requireActivity())
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.doante_menu, menu)
        menu.findItem(R.id.detailed_donate_data).apply {
            isChecked = isShowDetailed
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.detailed_donate_data -> {
                menuItem.isChecked = !menuItem.isChecked
                isShowDetailed = menuItem.isChecked
                requireActivity().putBoolean(SettingsPrefs, showDetailedKey, isShowDetailed)
                init(requireActivity())
            }
        }
        return true
    }
}
