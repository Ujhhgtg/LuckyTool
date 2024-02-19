package com.luckyzyx.luckytool.selector

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.databinding.DialogSortFilterSelectorLayoutBinding
import com.luckyzyx.luckytool.listener.OnSortChipListener

@Obfuscate
class SortFilterSelector(val context: Context) {

    private val binding =
        DialogSortFilterSelectorLayoutBinding.inflate(LayoutInflater.from(context))
    private var bottomSheet: BottomSheetDialog = BottomSheetDialog(context).apply {
        setContentView(binding.root)
    }

    private var onSortChipListener: OnSortChipListener? = null

    private var enableSort = false
    private var sortChips = ArrayList<String>()

    private var enableFilter = false
    private var filterChips = ArrayList<Chip>()

    init {
        initSortChips()
        initFilterChips()
    }

    private fun initSortChips() {
        binding.sortLayout.isVisible = enableSort
        binding.sortReverse.apply {
            setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed.not()) return@setOnCheckedChangeListener
                onSortChipListener?.onReverseChange(isChecked)
            }
        }
        binding.sortChips.apply {
            isSingleSelection = true
            sortChips.forEachIndexed { index, title ->
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
                onSortChipListener?.onSortModeChange(checkedIds.first() - 1)
            }
        }
    }

    private fun initFilterChips() {
        binding.filterLayout.isVisible = enableFilter
        binding.filterChips.apply {
            isSingleSelection = false
            filterChips.forEachIndexed { _, chip ->
                addView(chip)
            }
        }
    }

    fun setOnSortChipListener(onSortChipListener: OnSortChipListener) {
        this.onSortChipListener = onSortChipListener
    }

    fun setSortChips(enable: Boolean, chips: Array<String>?) {
        enableSort = enable
        if (chips != null) sortChips = ArrayList(chips.toList())
        initSortChips()
    }

    fun setFilterChips(enable: Boolean, chips: Array<Chip>?) {
        enableFilter = enable
        if (chips != null) filterChips = ArrayList(chips.toList())
        initFilterChips()
    }

    fun show() {
        if (enableSort || enableFilter) bottomSheet.show()
    }
}