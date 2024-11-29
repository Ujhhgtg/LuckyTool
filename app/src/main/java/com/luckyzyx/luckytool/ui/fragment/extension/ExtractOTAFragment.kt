package com.luckyzyx.luckytool.ui.fragment.extension

import android.annotation.SuppressLint
import android.content.Context
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
import com.luckyzyx.luckytool.databinding.FragmentExtractOtaBinding
import com.luckyzyx.luckytool.utils.AESCrypt
import com.luckyzyx.luckytool.utils.CommandUtils
import com.luckyzyx.luckytool.utils.DeviceUtils
import com.luckyzyx.luckytool.utils.FileUtils.cacheChild
import com.luckyzyx.luckytool.utils.SQLiteUtils
import com.luckyzyx.luckytool.utils.SQLiteUtils.getTableData
import com.luckyzyx.luckytool.utils.SQLiteUtils.readOnly
import com.luckyzyx.luckytool.utils.copyStr
import com.luckyzyx.luckytool.utils.formatFileSize
import com.luckyzyx.luckytool.utils.formatStringAuto
import com.luckyzyx.luckytool.utils.getFingerPrintModel
import com.luckyzyx.luckytool.utils.getModelMarketName
import com.luckyzyx.luckytool.utils.isZh
import com.luckyzyx.luckytool.utils.safeOfNull
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
                val command =
                    "${CommandUtils.cp} ${CommandUtils.otaDatabasePath} ${context.cacheDir}"
                ShellUtils.fastCmd(command)
                val dbFile = context.cacheChild("ota.db")
                val cursor = if (dbFile.exists()) {
                    val db = SQLiteUtils.openDataBase(dbFile.path, readOnly)
                    db?.getTableData("pkgList")
                } else null

                val list = ArrayList<String>()
                if (cursor != null) {
                    val packNameIndex = cursor.getColumnIndex("package_name")
//                    val versionNameIndex = cursor.getColumnIndex("version_name")
                    val sizeIndex = cursor.getColumnIndex("size")
                    val md5Index = cursor.getColumnIndex("md5")
                    val activeUrlIndex = cursor.getColumnIndex("active_url")
                    val urlIndex = cursor.getColumnIndex("url")

                    val otaList = ArrayList<String>()
                    while (cursor.moveToNext()) {
                        val packName = cursor.getStringOrNull(packNameIndex) ?: continue
                        val size = cursor.getStringOrNull(sizeIndex) ?: ""
                        val md5 = cursor.getStringOrNull(md5Index) ?: ""
                        val activeUrl = cursor.getStringOrNull(activeUrlIndex) ?: ""
                        val url = cursor.getStringOrNull(urlIndex) ?: ""

                        otaList.add(packName)
                        if (activeUrl.isNotBlank()) otaList.add("ActiveUrl: $activeUrl")
                        if (url.isNotBlank()) otaList.add("Url: $url")
                        if (md5.isNotBlank()) otaList.add("MD5: $md5")
                        if (size.isNotBlank()) otaList.add("Size: ${formatFileSize(size.toFloatOrNull())} ($size)")
                        otaList.add("")
                    }

                    if (otaList.isNotEmpty()) {
                        list.add("Model: ${getModelMarketName()} $getFingerPrintModel")
                        list.add("")
                        list.addAll(otaList)
                    } else return@withDefault list

                    val data = DeviceUtils.getOTACOnfigs()
                    val encrypt = safeOfNull {
                        AESCrypt.encrypt(data, CommandUtils.otaCryptKey, true)
                    } ?: ""
                    if (encrypt.isNotBlank()) list.add("Verity: $encrypt")
                    list.add("Source: #LuckyToolOTA")
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
            if (isZh(context)) binding.tips.text = "使用此功能时,禁止删除与遗漏数据"
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(requireActivity())
    }
}