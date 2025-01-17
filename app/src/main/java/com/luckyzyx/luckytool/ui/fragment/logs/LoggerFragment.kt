package com.luckyzyx.luckytool.ui.fragment.logs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.net.scope.AndroidScope
import com.drake.net.utils.scopeLife
import com.drake.net.utils.withDefault
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.log.data.YLogData
import com.highcapable.yukihookapi.hook.xposed.channel.annotation.SendTooLargeChannelData
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IGlobalFuncController
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogLogFilterLayoutBinding
import com.luckyzyx.luckytool.databinding.FragmentLogsBinding
import com.luckyzyx.luckytool.databinding.LayoutLoginfoItemBinding
import com.luckyzyx.luckytool.service.controller.GlobalFuncControllerService
import com.luckyzyx.luckytool.utils.AppUtils
import com.luckyzyx.luckytool.utils.FileUtils
import com.luckyzyx.luckytool.utils.FileUtils.cacheChild
import com.luckyzyx.luckytool.utils.IntentUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.bindRootService
import com.luckyzyx.luckytool.utils.copyStr
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.dp
import com.luckyzyx.luckytool.utils.formatDate
import com.luckyzyx.luckytool.utils.getDeviceInfo
import com.luckyzyx.luckytool.utils.setupMenuProvider
import com.luckyzyx.luckytool.utils.showToast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@Obfuscate
class LoggerFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentLogsBinding
    private var logFuncController: IGlobalFuncController? = null

    private var listData = ArrayList<YLogData>()
    private var logInfoViewAdapter: LogInfoViewAdapter? = null
    private var fileName: String = ""
    private var filterString = ""
    private var scope: AndroidScope? = null
    private lateinit var logsDir: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        binding = FragmentLogsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkDirs()
        logInfoViewAdapter = LogInfoViewAdapter(requireActivity(), listData)
        binding.loglistView.apply {
            adapter = logInfoViewAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener { loadLogger() }
        }
    }

    @OptIn(SendTooLargeChannelData::class)
    private fun loadLogger() {
        scope = scopeLife {
            listData.clear()
            binding.swipeRefreshLayout.isRefreshing = true
            binding.logNodataView.isVisible = false
            requireActivity().resources.getStringArray(R.array.xposed_scope).forEach { scope ->
                withDefault {
                    requireActivity().dataChannel(scope).allowSendTooLargeData()
                        .obtainLoggerInMemoryData { its ->
                            its.takeIf { e -> e.isNotEmpty() }?.run { listData.addAll(its) }
                            logInfoViewAdapter?.refreshDatas()
                        }
                }
            }
            binding.loglistView.isVisible = listData.isNotEmpty()
            binding.logNodataView.isVisible = listData.isEmpty()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()

        if (logFuncController == null) requireActivity().bindRootService(
            GlobalFuncControllerService::class.java, { _, iBinder ->
                logFuncController = IGlobalFuncController.Stub.asInterface(iBinder)
            })

        loadLogger()
    }

    override fun onPause() {
        super.onPause()
        scope?.cancel()
        scope?.close()
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.common_words_refresh)).apply {
            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 2, 0, getString(R.string.common_words_filter)).apply {
            setIcon(R.drawable.baseline_filter_list_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 3, 0, getString(R.string.common_words_save)).apply {
            setIcon(R.drawable.ic_baseline_save_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 4, 0, getString(R.string.common_words_share)).apply {
            setIcon(R.drawable.baseline_share_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == 1) loadLogger()
        if (menuItem.itemId == 2) {
            val binding = DialogLogFilterLayoutBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(requireActivity(), dialogCentered).apply {
                setTitle(getString(R.string.log_filter_title))
                setView(binding.root)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    logInfoViewAdapter?.getFilter?.filter(filterString)
                }
                setNeutralButton(android.R.string.cancel, null)
            }.show()
            binding.logFilter.apply {
                setText(filterString)
                addTextChangedListener(onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                    filterString = text.toString()
                })
            }
        }
        if (menuItem.itemId == 3) {
            fileName = "LuckyTool_" + formatDate("yyyyMMdd_HHmmss") + ".log"
            saveFile(fileName)
        }
        if (menuItem.itemId == 4) {
            fileName = "LuckyTool_" + formatDate("yyyyMMdd_HHmmss") + ".log"
            shareFile(fileName)
        }
        return true
    }

    private val createDocument = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/log")
    ) {
        if (it != null) {
//                val dir = it.path?.split(":")?.get(1) ?: "/sdcard/Download/$fileName"
//                YukiHookLogger.saveToFile(dir,listData)
            alterDocument(requireActivity(), it)
        }
    }

    private fun saveFile(fileName: String) {
        checkDirs()
        if (listData.isEmpty()) requireActivity().showToast(getString(R.string.log_data_is_empty))
        else if (IntentUtils(requireActivity()).checkCreateDocument()) {
            createDocument.launch(fileName)
        } else requireActivity().showToast("Intent Create Document Error!")
    }

    private fun shareFile(fileName: String) {
        checkDirs()
        if (listData.isEmpty()) requireActivity().showToast(getString(R.string.log_data_is_empty))
        else {
            logsDir.mkdirs()
            val logFile = File(logsDir, fileName)
            if (!logFile.exists()) logFile.createNewFile()
            logFile.writeText(getLogsString(requireActivity()))
            FileUtils.shareFile(requireActivity(), "Share", logFile)
        }
    }

    private fun checkDirs() {
        FileUtils.checkDownloadDir(requireActivity(), "LuckyTool")

        logsDir = requireActivity().cacheChild("logs")
        logsDir.listFiles()?.forEach { if (it.exists()) it.delete() }
        if (logsDir.exists()) logsDir.delete()
    }

    private fun alterDocument(context: Context, uri: Uri) {
        val str = getLogsString(context)
        try {
            context.contentResolver.openFileDescriptor(uri, "w")?.use { its ->
                FileOutputStream(its.fileDescriptor).use {
                    it.write(str.toByteArray())
                }
            }
            context.showToast(getString(R.string.log_save_success))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            context.showToast(getString(R.string.log_save_failed))
        } catch (e: IOException) {
            e.printStackTrace()
            context.showToast(getString(R.string.log_save_failed))
        }
    }

    private fun getLogsString(context: Context): String {
        var str = ""
        str += context.getDeviceInfo(logFuncController, true)
        str += "\n\n"
        listData.forEach {
            val time = formatDate("yyyy/MM/dd-HH:mm:ss", it.timestamp)
            val messageFinal = if (it.msg != "null") "\nMessage -> ${it.msg}" else ""
            val throwableFinal =
                if (it.throwable.toString() != "null") "\nThrowable -> ${it.throwable}\n\n" else "\n\n"
            str += "[${time}][${it.tag}][${it.priority}][${it.packageName}][${it.userId}]$messageFinal$throwableFinal"
        }
        return str
    }
}

class LogInfoViewAdapter(val context: Context, data: ArrayList<YLogData>) :
    RecyclerView.Adapter<LogInfoViewAdapter.ViewHolder>() {

    private var allData = ArrayList<YLogData>()
    private var filterData = ArrayList<YLogData>()

    init {
        allData = data
        filterData = allData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutLoginfoItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val time = formatDate("yyyy/MM/dd-HH:mm:ss", filterData[position].timestamp)
        val tag = filterData[position].tag
        val priority = filterData[position].priority
        val packageName = filterData[position].packageName
        val userId = filterData[position].userId
        val msg = filterData[position].msg
        val throwable = filterData[position].throwable.toString()
        holder.logIcon.setImageDrawable(AppUtils(context).getAppIcon(packageName))
        holder.logTime.text = time
        holder.logMsg.text = msg
        holder.logRoot.setOnClickListener(null)
        holder.logRoot.setOnClickListener {
            MaterialAlertDialogBuilder(context, dialogCentered).apply {
                setTitle(AppUtils(context).getAppLabel(packageName))
                setView(NestedScrollView(context).apply {
                    addView(MaterialTextView(context).apply {
                        setPadding(20.dp, 0, 20.dp, 20.dp)
                        text = msg + if (throwable != "null") "\n\n$throwable" else ""
                    })
                })
                setPositiveButton(android.R.string.copy) { _, _ ->
                    val messageFinal = if (msg != "null") "\nMessage -> $msg" else ""
                    val throwableFinal =
                        if (throwable != "null") "\nThrowable -> ${throwable}\n\n" else "\n\n"
                    context.copyStr("[${time}][${tag}][${priority}][${packageName}][${userId}]$messageFinal$throwableFinal")
                }
            }.show()
        }
    }

    override fun getItemCount(): Int {
        return filterData.size
    }

    val getFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterStr = constraint.toString().lowercase()
            filterData = if (constraint.isBlank()) allData
            else {
                val filterlist = ArrayList<YLogData>()
                allData.forEach {
                    if (it.toString().lowercase().contains(filterStr)) filterlist.add(it)
                }
                filterlist
            }
            val filterResults = FilterResults()
            filterResults.values = filterData
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filterData = results.values as ArrayList<YLogData>
            refreshDatas()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshDatas() {
        notifyDataSetChanged()
    }

    class ViewHolder(binding: LayoutLoginfoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val logRoot: ConstraintLayout = binding.root
        val logIcon: ImageView = binding.logIcon
        val logTime: TextView = binding.logTime
        val logMsg: TextView = binding.logMsg
    }
}