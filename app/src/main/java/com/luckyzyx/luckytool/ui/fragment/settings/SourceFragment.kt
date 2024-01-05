package com.luckyzyx.luckytool.ui.fragment.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.R

@Obfuscate
class SourceFragment : ModulePreferenceFragment() {
    override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireActivity()).apply {
            addPreference(PreferenceCategory(context).apply {
                setTitle(R.string.open_source)
                isIconSpaceReserved = false
            })
            addPreference(Preference(context).apply {
                title = "Xposed"
                summary = "rovo89 , Apache License 2.0"
                isIconSpaceReserved = false
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rovo89/Xposed"))
            })
            addPreference(Preference(context).apply {
                title = "LSPosed"
                summary = "LSPosed , GPL-3.0 License"
                isIconSpaceReserved = false
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LSPosed/LSPosed"))
            })
            addPreference(Preference(context).apply {
                title = "YukiHookAPI"
                summary = "fankes , MIT License"
                isIconSpaceReserved = false
                intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")
                )
            })
            addPreference(Preference(context).apply {
                title = "ColorOSNotifyIcon"
                summary = "fankes , AGPL-3.0 License"
                isIconSpaceReserved = false
                intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/ColorOSNotifyIcon")
                )
            })
            addPreference(Preference(context).apply {
                title = "ColorOSTool"
                summary = "Oosl , GPL-3.0 License"
                isIconSpaceReserved = false
                intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Oosl/ColorOSTool"))
            })
            addPreference(Preference(context).apply {
                title = "WooBoxForColorOS"
                summary = "Simplicity-Team , GPL-3.0 License"
                isIconSpaceReserved = false
                intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Simplicity-Team/WooBoxForColorOS")
                )
            })
            addPreference(Preference(context).apply {
                title = "CorePatch"
                summary = "LSPosed , GPL-2.0 license"
                isIconSpaceReserved = false
                intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://github.com/LSPosed/CorePatch")
                )
            })
            addPreference(Preference(context).apply {
                title = "DisableFlagSecure"
                summary = "LSPosed , GPL-3.0 license"
                isIconSpaceReserved = false
                intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://github.com/LSPosed/DisableFlagSecure")
                )
            })
            addPreference(Preference(context).apply {
                title = "FivegTile"
                summary = "libxzr , MIT license"
                isIconSpaceReserved = false
                intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://github.com/libxzr/FivegTile")
                )
            })
            addPreference(Preference(context).apply {
                title = "WooBoxForMIUI"
                summary = "LittleTurtle2333 , GPL-3.0 license"
                isIconSpaceReserved = false
                intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Simplicity-Team/WooBoxForMIUI")
                )
            })
        }
    }
}
