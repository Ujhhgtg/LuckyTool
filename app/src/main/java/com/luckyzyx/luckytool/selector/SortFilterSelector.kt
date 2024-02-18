package com.luckyzyx.luckytool.selector

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.DialogAppinfoSortFilterSheetBinding
import com.luckyzyx.luckytool.listener.OnSortFilterListener

@Obfuscate
class SortFilterSelector(val context: Context) {

    private val binding = DialogAppinfoSortFilterSheetBinding.inflate(LayoutInflater.from(context))
    private var bottomSheet: BottomSheetDialog = BottomSheetDialog(context).apply {
        setContentView(binding.root)
    }

    private var onSortFilterListener: OnSortFilterListener? = null

    init {
        binding.sortReverse.apply {
            setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                onSortFilterListener?.onReverseChange(isChecked)
                onSortFilterListener?.onRefreshData()
            }
        }
        binding.sortChips.apply {
            isSingleSelection = true
            arrayOf(
                context.getString(R.string.appinfo_app_name),
                context.getString(R.string.appinfo_package_name),
                context.getString(R.string.appinfo_app_size),
                context.getString(R.string.appinfo_install_time),
                context.getString(R.string.appinfo_last_updated_time),
                context.getString(R.string.appinfo_target_sdk)
            ).forEachIndexed { index, title ->
                val chip = Chip(context).apply {
                    text = title
                    isCheckable = true
                    isClickable = true
                    isChecked = index == 0
                }
                addView(chip)
            }
            setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
                onSortFilterListener?.onSortModeChange(checkedIds.first() - 1)
                onSortFilterListener?.onRefreshData()
            }
        }
        binding.filterChips.apply {
            isSingleSelection = false
            arrayOf(context.getString(R.string.appinfo_system_app)).forEachIndexed { index, title ->
                val chip = Chip(context).apply {
                    text = title
                    isCheckable = true
                    isClickable = true
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                        when (index) {
                            0 -> {
                                onSortFilterListener?.onShowSystemChange(isChecked)
                                onSortFilterListener?.onRefreshData()
                            }
                        }
                    }
                }
                addView(chip)
            }
        }
    }

    fun setOnSortFilterListener(onSortFilterListener: OnSortFilterListener) {
        this.onSortFilterListener = onSortFilterListener
    }

    fun show() {
        bottomSheet.show()
    }
}