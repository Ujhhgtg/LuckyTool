package com.luckyzyx.luckytool.service.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.RemoteException
import android.os.ServiceManager
import android.telephony.TelephonyManager
import com.android.internal.telephony.ITelephony
import com.android.internal.telephony.RILConstants
import com.joom.paranoid.Obfuscate
import com.luckyzyx.luckytool.IFiveGController
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.SDK
import com.topjohnwu.superuser.ipc.RootService

@Obfuscate
class FiveGControllerService : RootService() {

    companion object {

        private val telephonyService by lazy {
            ServiceManager.getService(Context.TELEPHONY_SERVICE)
        }

        private val iTelephony by lazy {
            ITelephony.Stub.asInterface(telephonyService)
        }

        /**
         * 指示用户请求允许的网络类型更改
         */
        @SuppressLint("InlinedApi")
        val reasonUser = TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER

        /**
         * 网络类型位掩码，指示无线电技术 NR（新无线电）5G 的支持
         */
        @SuppressLint("InlinedApi")
        val bitMaskNR = TelephonyManager.NETWORK_TYPE_BITMASK_NR

        /**
         * 首选网络模式为 TD-SCDMA/LTE/GSM/WCDMA、CDMA 和 EvDo
         */
        val modeLTE = RILConstants.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA

        /**
         * 首选网络模式是 NR 5G、LTE、TD-SCDMA、CDMA、EVDO、GSM 和 WCDMA
         */
        val modeNR = RILConstants.NETWORK_MODE_NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA
    }

    override fun onBind(intent: Intent) = object : IFiveGController.Stub() {
        @SuppressLint("DeprecatedSinceApi")
        override fun checkCompatibility(subId: Int): Boolean {
            return try {
                if (SDK >= A12) {
                    val types = iTelephony.getAllowedNetworkTypesForReason(subId, reasonUser)
                    iTelephony.setAllowedNetworkTypesForReason(subId, reasonUser, types)
                } else {
                    // For Q and R.
                    val types = iTelephony.getPreferredNetworkType(subId)
                    iTelephony.setPreferredNetworkType(subId, types)
                }
            } catch (_: Throwable) {
                false
            } catch (_: RemoteException) {
                false
            }
        }

        @SuppressLint("DeprecatedSinceApi")
        override fun getFiveGStatus(subId: Int): Boolean {
            return try {
                if (SDK >= A12) {
                    iTelephony.getAllowedNetworkTypesForReason(
                        subId, reasonUser
                    ) and bitMaskNR != 0L
                } else {
                    // For Q and R.
                    iTelephony.getPreferredNetworkType(subId) == modeNR
                }
            } catch (_: Throwable) {
                false
            } catch (_: RemoteException) {
                false
            }
        }

        @SuppressLint("DeprecatedSinceApi")
        override fun setFiveGStatus(subId: Int, enabled: Boolean) {
            try {
                if (SDK >= A12) {
                    var curTypes = iTelephony.getAllowedNetworkTypesForReason(subId, reasonUser)
                    curTypes = if (enabled) curTypes or bitMaskNR
                    else curTypes and bitMaskNR.inv()
                    iTelephony.setAllowedNetworkTypesForReason(subId, reasonUser, curTypes)
                } else {
                    // For Q and R.
                    iTelephony.setPreferredNetworkType(subId, if (enabled) modeNR else modeLTE)
                }
            } catch (_: Throwable) {

            } catch (_: RemoteException) {

            }
        }
    }
}