package me.garfieldhan.cherish.domesystem;

import android.util.Log;

import java.util.List;

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
    
    public static native String s(int i); // getString
    
    public static native float f(int i); // getFloat
    
    public static native int i(int i); // getInt
    
    public static native long l(int i); // getLong
    
    public static native List<?> t(int i); // getList
    
    public static native String d(String s); // CherishCipher -> decrypt
    
    public static native String e(String s); // CherishCipher -> encrypt
}
