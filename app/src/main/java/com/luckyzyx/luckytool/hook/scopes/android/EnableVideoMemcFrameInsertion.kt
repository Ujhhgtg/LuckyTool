package com.luckyzyx.luckytool.hook.scopes.android

import android.util.ArraySet
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.luckyzyx.luckytool.data.MemcConfigActivity
import com.luckyzyx.luckytool.data.MemcConfigPackage
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode

object EnableVideoMemcFrameInsertion : YukiBaseHooker() {

    private val allConfigPackages = ArrayList<MemcConfigPackage>()
    private val allConfigActivitys = ArrayList<MemcConfigActivity>()

    private val configAppList = java.util.ArrayList<String>()
    private val appScreenRate = HashMap<String, String>()
    private val sdr2hdrCommand = HashMap<String, String>()
    private val configActivityList = java.util.ArrayList<String>()
    private val configMemcCommand = HashMap<String, String>()
    override fun onHook() {
        if (getOSVersionCode < 26) return
        val isEnable = prefs(ModulePrefs).getBoolean("enable_video_memc_frame_insertion", false)
        val configPackages =
            prefs(ModulePrefs).getStringSet("memc_config_package_list", ArraySet())
        val configActivitys =
            prefs(ModulePrefs).getStringSet("memc_config_activity_list", ArraySet())

        //Source OplusMemcHelper
        "com.android.server.display.memc.OplusMemcHelper".toClass().apply {
            method { name = "init" }.hook {
                after {
                    if (!isEnable) return@after
                    init(configPackages, configActivitys)
                }
            }
            method { name = "getConfigAppList" }.hook {
                after {
                    if (!isEnable || configAppList.isEmpty()) return@after
                    result = configAppList
                }
            }
            method { name = "getAppScreenRateMap" }.hook {
                after {
                    if (!isEnable || appScreenRate.isEmpty()) return@after
                    result = appScreenRate
                }
            }
            method { name = "getSdr2hdrCommandMap" }.hook {
                after {
                    if (!isEnable || sdr2hdrCommand.isEmpty()) return@after
                    result = sdr2hdrCommand
                }
            }
            method { name = "getConfigActivityList" }.hook {
                after {
                    if (!isEnable || configActivityList.isEmpty()) return@after
                    result = configActivityList
                }
            }
            method { name = "getMemcCommandMap" }.hook {
                after {
                    if (!isEnable || configMemcCommand.isEmpty()) return@after
                    result = configMemcCommand
                }
            }
        }
    }

    fun init(configPackages: Set<String>, configActivitys: Set<String>) {
        allConfigPackages.clear()
        allConfigActivitys.clear()
        configPackages.forEach {
            val configPackageInfo = MemcConfigPackage().toMemcConfigPackage(it)
            if (configPackageInfo != null) allConfigPackages.add(configPackageInfo)
        }
        configActivitys.forEach {
            val configActivityInfo = MemcConfigActivity().toMemcConfigActivity(it)
            if (configActivityInfo != null) allConfigActivitys.add(configActivityInfo)
        }

        configAppList.clear()
        appScreenRate.clear()
        sdr2hdrCommand.clear()
        configActivityList.clear()
        configMemcCommand.clear()

        allConfigPackages.forEach {
            configAppList.add(it.packName)
            appScreenRate[it.packName] = it.rate
            sdr2hdrCommand[it.packName] = it.type
        }
        allConfigActivitys.forEach {
            configActivityList.add(it.activity)
            configMemcCommand["${it.packName}/${it.activity}"] = it.type
        }
    }
}