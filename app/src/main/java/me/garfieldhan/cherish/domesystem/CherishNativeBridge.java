package me.garfieldhan.cherish.domesystem;

import android.util.Log;

public class CherishNativeBridge {
    private static boolean mInitializeCompleted = false;
    
    static {
        initializeIfNeed();
    }
    
    public synchronized static void initializeIfNeed() {
        if (!mInitializeCompleted) {
            synchronized (CherishNativeBridge.class) {
                long currentTimeMillis = System.currentTimeMillis();
                System.loadLibrary("cherish_domestub");
                long finishTimeMillis = System.currentTimeMillis() - currentTimeMillis;
                Log.i("GarfieldHan", "[initialize@CherishDomeStubSDK] sdk_version=CherishDomeSDK-v1.0.0-5501077, cost time: " + finishTimeMillis + " ms");
                mInitializeCompleted = true;
            }
        }
    }
    
    public static native String a(int i); // getString
    
    public static native String d(String s); // CherishCipher -> decrypt
    
    public static native String e(String s); // CherishCipher -> encrypt
}
