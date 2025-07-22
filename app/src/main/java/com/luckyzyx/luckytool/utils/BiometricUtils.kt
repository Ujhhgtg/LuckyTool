package com.luckyzyx.luckytool.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import com.luckyzyx.luckytool.BuildConfig
import org.lsposed.lsparanoid.Obfuscate
import java.util.concurrent.Executor

@Obfuscate
object BiometricUtils {

    val TAG = "BiometricUtils"

    fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().apply {
            setTitle("Biometric")
            setSubtitle(BuildConfig.APPLICATION_ID)
            setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        }.build()
    }

    fun showBiometricPrompt(
        activity: AppCompatActivity, callback: BiometricPrompt.AuthenticationCallback
    ) {
        return BiometricPrompt(activity, activity.mainExecutor, callback).authenticate(
            createPromptInfo()
        )
    }

    fun showBiometricPrompt(
        fragemnt: Fragment, executor: Executor, callback: BiometricPrompt.AuthenticationCallback
    ) {
        return BiometricPrompt(fragemnt, executor, callback).authenticate(createPromptInfo())
    }

}