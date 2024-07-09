package com.luckyzyx.luckytool.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeLife
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.ActivityMainBinding
import com.luckyzyx.luckytool.ui.fragment.home.HomeFragment
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.AESCrypt
import com.luckyzyx.luckytool.utils.AppAnalyticsUtils.checkAppForbiddenList
import com.luckyzyx.luckytool.utils.AppAnalyticsUtils.checkGitlabBlackList
import com.luckyzyx.luckytool.utils.FileUtils
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.OtherPrefs
import com.luckyzyx.luckytool.utils.PermissionUtils
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.ShortcutUtils
import com.luckyzyx.luckytool.utils.ThemeUtils
import com.luckyzyx.luckytool.utils.checkVerify
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.exitModule
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.getPcbInfo
import com.luckyzyx.luckytool.utils.getPrjNameInfo
import com.luckyzyx.luckytool.utils.getSnInfo
import com.luckyzyx.luckytool.utils.getString
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.putString
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import kotlinx.coroutines.Dispatchers
import me.garfieldhan.cherish.domesystem.CherishNativeBridge
import java.io.File
import java.io.InputStream
import kotlin.system.exitProcess

@Obfuscate
@Suppress("PrivatePropertyName")
open class MainActivity : AppCompatActivity() {
    //检测Prefs状态
    private var isModuleActive = YukiHookAPI.Status.isXposedModuleActive
    private val KEY_PREFIX = MainActivity::class.java.name + '.'
    private val EXTRA_SAVED_INSTANCE_STATE = KEY_PREFIX + "SAVED_INSTANCE_STATE"

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private fun newIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    private fun newIntent(savedInstanceState: Bundle, context: Context): Intent {
        return newIntent(context).putExtra(EXTRA_SAVED_INSTANCE_STATE, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTheme()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationFragment()
        initDynamicShortcuts()
        checkVerify()
        checkSuAndOS()

        installDomeStubData()
    }

    private fun installDomeStubData() {
        val dialog = MaterialAlertDialogBuilder(this, dialogCentered).apply {
            setMessage("Loading...")
        }.create()
        scopeDialog(dialog, false, Dispatchers.Default) {
            val tmpDir = "/data/luckytool/"
            val tmpFile = "/data/luckytool/data.dat"
            val dataCacheFile = File(codeCacheDir, "data.dat").apply {
                if (exists()) delete()
                createNewFile()
            }
            ShellUtils.fastCmd("mkdir -p $tmpDir && chmod 0777 $tmpDir && chown root:root $tmpDir && chcon u:object_r:system_file:s0 $tmpDir")
            ShellUtils.fastCmd("rm -rf $tmpFile && touch $tmpFile")
            val inputStream: InputStream = assets.open("data.dat")
            FileUtils.copyStreamToFile(inputStream, dataCacheFile)
            ShellUtils.fastCmd("cp -fpr ${dataCacheFile.path} $tmpFile")
            ShellUtils.fastCmd("chmod 0777 $tmpFile && chown root:root $tmpFile && chcon u:object_r:system_file:s0 $tmpFile")
        }.catch {
            LogUtils.e("installDomeStubData", "data", it.toString(), true)
        }
    }

    private fun checkSuAndOS() {
        val noModulePrefs = prefs(ModulePrefs).isPreferencesAvailable.not()
        val noSettingPrefs = prefs(SettingsPrefs).isPreferencesAvailable.not()
        val noOtherPrefs = prefs(OtherPrefs).isPreferencesAvailable.not()
        if (!isModuleActive || noModulePrefs || noSettingPrefs || noOtherPrefs) {
            MaterialAlertDialogBuilder(this).apply {
                setCancelable(false)
                setMessage(getString(R.string.unsupported_xposed, "v1.9.2"))
                setPositiveButton(android.R.string.ok) { _, _ -> exitProcess(0) }
                setOnDismissListener { exitModule() }
                show()
            }
            return
        }
        val isSu = Shell.getShell().isRoot
        putBoolean(SettingsPrefs, "is_su", isSu)
        putBoolean(SettingsPrefs, "settings_prefs", isSu)
        putBoolean(ModulePrefs, "module_prefs", isSu)
        putBoolean(OtherPrefs, "other_prefs", isSu)
        if (!isSu) {
            MaterialAlertDialogBuilder(this, dialogCentered).apply {
                setCancelable(false)
                setTitle(getString(R.string.no_root))
                setMessage(getString(R.string.no_root_summary))
                setPositiveButton(android.R.string.ok) { _, _ -> exitProcess(0) }
                setOnDismissListener { exitModule() }
                show()
            }
            return
        }
        if (getOSVersionCode < 23) {
            val current = navController.currentDestination.toString()
            MaterialAlertDialogBuilder(this, dialogCentered).apply {
                setCancelable(false)
                setTitle(getString(R.string.unsupported_os))
                setMessage(getString(R.string.unsupported_os_summary))
                setPositiveButton(android.R.string.ok) { _, _ -> exitProcess(0) }
                if (getOSVersionCode > 0) setNeutralButton(
                    getString(R.string.common_words_ignore), null
                )
                if (current.contains(HomeFragment::class.java.simpleName)) show()
            }
        }
        putBoolean(SettingsPrefs, "enable_module_print_logs", BuildConfig.DEBUG)
        PermissionUtils(this).start()
        scopeLife(dispatcher = Dispatchers.IO) {
            checkAppForbiddenList()
            checkGitlabBlackList()
        }
        saveDeviceInfos()
    }

    private fun saveDeviceInfos() {
        try {
            putString(
                SettingsPrefs,
                AESCrypt.encrypt(CherishNativeBridge.s(2)),
                getPcbInfo.takeIf { it != "null" } ?: "")
            putString(
                SettingsPrefs,
                AESCrypt.encrypt(CherishNativeBridge.s(3)),
                getSnInfo.takeIf { it != "null" } ?: "")
            putString(
                SettingsPrefs,
                AESCrypt.encrypt(CherishNativeBridge.s(4)),
                getPrjNameInfo.takeIf { it != "null" } ?: "")
        } catch (t: Throwable) {

        }
    }

    private fun initDynamicShortcuts() {
        if (!ShortcutUtils(this).getIconStatus()) return
        if (ShortcutUtils(this).getEnabledShortcutList().isEmpty()) return
        ShortcutUtils(this).updateDynamicShortcuts()
    }

    private fun initNavigationFragment() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_other,
            R.id.nav_function,
            R.id.nav_home,
            R.id.nav_log,
            R.id.nav_setting,
        ).build()
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.navView.apply {
            labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_SELECTED
            setupWithNavController(navController)
            setOnItemSelectedListener {
                NavigationUI.onNavDestinationSelected(it, navController)
                true
            }
        }
    }

    private fun initTheme() {
        ThemeUtils.initDynamicColor(this)
        val themeMode = getString(SettingsPrefs, "dark_theme", "MODE_NIGHT_FOLLOW_SYSTEM")
        ThemeUtils.initTheme(themeMode)
    }

    @Suppress("DEPRECATION")
    fun restart() {
        if (SDK >= A12 || !Process.isApplicationUid(Process.myUid())) {
            recreate()
        } else {
            try {
                val savedInstanceState = Bundle()
                onSaveInstanceState(savedInstanceState)
                finish()
                startActivity(newIntent(savedInstanceState, this))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } catch (e: Throwable) {
                recreate()
            }
        }
    }
}