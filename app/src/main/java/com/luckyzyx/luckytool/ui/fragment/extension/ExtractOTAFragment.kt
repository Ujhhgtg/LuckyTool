package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.database.getStringOrNull
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.FragmentExtractOtaBinding
import com.luckyzyx.luckytool.utils.FileUtils.cacheChild
import com.luckyzyx.luckytool.utils.SQLiteUtils
import com.luckyzyx.luckytool.utils.SQLiteUtils.getTableData
import com.luckyzyx.luckytool.utils.SQLiteUtils.readOnly
import com.luckyzyx.luckytool.utils.copyStr
import com.luckyzyx.luckytool.utils.formatFileSize
import com.luckyzyx.luckytool.utils.formatStringAuto
import com.luckyzyx.luckytool.utils.getModelMarketName
import com.topjohnwu.superuser.ShellUtils

@Obfuscate
class ExtractOTAFragment : Fragment() {

    private lateinit var binding: FragmentExtractOtaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentExtractOtaBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SdCardPath")
    fun init(context: Context) {
        scopeLife {
            binding.swipeRefreshLayout.apply {
                setOnRefreshListener { init(context) }
                isRefreshing = true
            }
            binding.otaData.isVisible = false
            binding.noOtaData.isVisible = true
            binding.copyOtaData.isEnabled = false

            val dataList = withDefault {
                val command = "cp /data/user/0/com.oplus.ota/databases/ota.db ${context.cacheDir}"
                ShellUtils.fastCmd(command)
                val dbFile = context.cacheChild("ota.db")
                val cursor = if (dbFile.exists()) {
                    val db = SQLiteUtils.openDataBase(dbFile.path, readOnly)
                    db?.getTableData("pkgList")
                } else null

                val list = ArrayList<String>()
                if (cursor != null) {
                    val packNameIndex = cursor.getColumnIndex("package_name")
                    val sizeIndex = cursor.getColumnIndex("size")
                    val md5Index = cursor.getColumnIndex("md5")
                    val activeUrlIndex = cursor.getColumnIndex("active_url")
                    val urlIndex = cursor.getColumnIndex("url")

                    while (cursor.moveToNext()) {
                        val packName = cursor.getStringOrNull(packNameIndex) ?: "Null"
                        val size = cursor.getStringOrNull(sizeIndex) ?: "Null"
                        val md5 = cursor.getStringOrNull(md5Index) ?: "Null"
                        val activeUrl = cursor.getStringOrNull(activeUrlIndex) ?: "Null"
                        val url = cursor.getStringOrNull(urlIndex) ?: "Null"

                        list.add("Model: ${getModelMarketName()} ${Build.MODEL}")
                        list.add("")
                        list.add("PackName: $packName")
                        list.add("")
                        list.add("Size: ${formatFileSize(size.toFloatOrNull())} ($size)")
                        list.add("")
                        list.add("ActiveUrl: $activeUrl")
                        list.add("")
                        list.add("Url: $url")
                        list.add("")
                        list.add("MD5: $md5")
                        list.add("")
                        list.add("${getString(R.string.extract_ota_source)}: @LuckyTool")
                    }
                }
                list
            }
            if (dataList.isNotEmpty()) {
                binding.otaData.apply {
                    text = formatStringAuto(dataList, "\n")
                    isVisible = text.isNotBlank()
                }
                binding.noOtaData.isVisible = !binding.otaData.isVisible
                binding.copyOtaData.apply {
                    isEnabled = binding.otaData.isVisible
                    setOnClickListener { context.copyStr(binding.otaData.text) }
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(requireActivity())
    }
}