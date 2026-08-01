package com.luckyzyx.luckytool.ui.fragment.logs

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.FragmentLogsBinding
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.setupMenuProvider

class LoggerFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentLogsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupMenuProvider(this)
        binding = FragmentLogsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.add(0, 1, 0, getString(R.string.refresh)).apply {
            setIcon(R.drawable.ic_baseline_refresh_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 2, 0, getString(R.string.filter)).apply {
            setIcon(R.drawable.baseline_filter_list_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 3, 0, getString(R.string.save)).apply {
            setIcon(R.drawable.ic_baseline_save_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
        menu.add(0, 4, 0, getString(R.string.share)).apply {
            setIcon(R.drawable.baseline_share_24)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            if (ThemeUtils.isNightMode(resources.configuration)) {
                iconTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }
}