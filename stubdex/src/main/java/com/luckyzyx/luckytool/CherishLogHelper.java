package com.luckyzyx.luckytool;

import android.util.Log;

public class CherishLogHelper {
    private static final String TAG = "GarfieldHan";
    private static final boolean bIsDebugEnabled = true; // 日志开关，外发版本一定要关闭，否则会输出机密内容
    
    public static void i(String msg) {
        if (bIsDebugEnabled) {
            Log.i(TAG, msg);
        }
    }
    
    public static void e(String msg) {
        if (bIsDebugEnabled) {
            Log.e(TAG, msg);
        }
    }
}