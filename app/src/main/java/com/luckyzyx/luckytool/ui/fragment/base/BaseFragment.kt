package com.luckyzyx.luckytool.ui.fragment.base

import androidx.viewbinding.ViewBinding
import com.highcapable.betterandroid.ui.component.fragment.AppBindingFragment
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
abstract class BaseFragment<VH : ViewBinding> : AppBindingFragment<VH>()