package com.luckyzyx.luckytool.ui.activity

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.luckyzyx.luckytool.BuildConfig
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.databinding.ActivityMainBinding
import com.luckyzyx.luckytool.service.ActivityManagerService
import com.luckyzyx.luckytool.service.AdbService
import com.luckyzyx.luckytool.service.GlobalFuncService
import com.luckyzyx.luckytool.service.PackagesService
import com.luckyzyx.luckytool.service.PowerService
import com.luckyzyx.luckytool.service.RefreshRateService
import com.luckyzyx.luckytool.service.TilesService
import com.luckyzyx.luckytool.service.UserService
import com.luckyzyx.luckytool.ui.activity.base.BaseActivity
import com.luckyzyx.luckytool.ui.fragment.home.HomeFragment
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.BiometricUtils
import com.luckyzyx.luckytool.utils.DeviceUtils
import com.luckyzyx.luckytool.utils.IntentPrefs
import com.luckyzyx.luckytool.utils.LogUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.OtherPrefs
import com.luckyzyx.luckytool.utils.PermissionUtils
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.SettingsPrefs
import com.luckyzyx.luckytool.utils.dialogCentered
import com.luckyzyx.luckytool.utils.exitModule
import com.luckyzyx.luckytool.utils.getBoolean
import com.luckyzyx.luckytool.utils.getOSVersionCode
import com.luckyzyx.luckytool.utils.putBoolean
import com.luckyzyx.luckytool.utils.verityPackage
import org.lsposed.lsparanoid.Obfuscate
import kotlin.system.exitProcess

@Obfuscate
@Suppress("PrivatePropertyName")
open class MainActivity : BaseActivity() {
    //检测Prefs状态
    private var isModuleActive = YukiHookAPI.Status.isXposedModuleActive
    private val KEY_PREFIX = MainActivity::class.java.name + '.'
    private val EXTRA_SAVED_INSTANCE_STATE = KEY_PREFIX + "SAVED_INSTANCE_STATE"

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    private fun newIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    private fun newIntent(savedInstanceState: Bundle, context: Context): Intent {
        return newIntent(context).putExtra(EXTRA_SAVED_INSTANCE_STATE, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationFragment()

        verityPackage()
        checkXposed()
        checkOs()
        checkBiometric()
    }

    private fun checkXposed() {
        val prefsArray = arrayOf(ModulePrefs, SettingsPrefs, IntentPrefs, OtherPrefs)
        val prefsStatus = prefsArray.map { prefs(it).isPreferencesAvailable }
        if (!isModuleActive || prefsStatus.contains(false)) {
            MaterialAlertDialogBuilder(this).apply {
                setCancelable(false)
                setMessage(getString(R.string.unsupported_xposed))
                setPositiveButton(android.R.string.ok) { _, _ -> exitProcess(0) }
                setOnDismissListener { exitModule() }
                show()
            }
            return
        }
    }

    private fun checkSu() {
        val isSu = DeviceUtils.getRootStatus()
        putBoolean(SettingsPrefs, "is_su", isSu)
        putBoolean(SettingsPrefs, "settings_prefs", isSu)
        putBoolean(ModulePrefs, "module_prefs", isSu)
        putBoolean(IntentPrefs, "intent_prefs", isSu)
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
        putBoolean(SettingsPrefs, "enable_module_print_logs", BuildConfig.DEBUG)
        PermissionUtils(this).start()
    }

    private fun checkOs() {
        val osCode = getOSVersionCode
        val current = navController.currentDestination.toString()
        MaterialAlertDialogBuilder(this, dialogCentered).apply {
            setCancelable(false)
            setTitle(getString(R.string.unsupported_os))
            setMessage(getString(R.string.unsupported_os_summary))
            setPositiveButton(android.R.string.ok) { _, _ -> exitProcess(0) }
            if (osCode > 0) setNeutralButton(getString(R.string.common_words_ignore), null)
            if (osCode < 23 && current.contains(HomeFragment::class.java.simpleName)) show()
        }
    }

    private fun checkBiometric() {
        val enable = getBoolean(SettingsPrefs, "enable_biometric_unlock_verification", false)
        val manager = getSystemService(KeyguardManager::class.java)
        if (enable && manager.isDeviceSecure) {
            BiometricUtils.showBiometricPrompt(
                this, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int, errString: CharSequence
                    ) {
                        finish()
                        LogUtils.d(
                            "checkBiometric", "onAuthenticationError",
                            "$errorCode, $errString", true
                        )
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        LogUtils.d(
                            "checkBiometric", "onAuthenticationSucceeded",
                            "${result.authenticationType}", true
                        )
                    }

                    override fun onAuthenticationFailed() {
                        finish()
                        LogUtils.d("checkBiometric", "onAuthenticationFailed", "", true)
                    }
                })
        }
    }

    private fun initAllService() {
        GlobalFuncService.init(this)
        PackagesService.init(this)
        TilesService.init(this)
        RefreshRateService.init(this)
        AdbService.init(this)
        ActivityManagerService.init(this)
        UserService.init(this)
        PowerService.init(this)
    }

    override fun onResume() {
        super.onResume()
        checkSu()
        initAllService()
    }

    private fun initNavigationFragment() {
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
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