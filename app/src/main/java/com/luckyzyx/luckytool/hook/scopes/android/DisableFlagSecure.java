package com.luckyzyx.luckytool.hook.scopes.android;

import static com.luckyzyx.luckytool.utils.SPUtilsKt.ModulePrefs;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.luckyzyx.luckytool.BuildConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DisableFlagSecure implements IXposedHookLoadPackage {
    final XSharedPreferences prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, ModulePrefs);
    private final static Method deoptimizeMethod;
    
    static {
        Method m = null;
        try {
            //noinspection JavaReflectionMemberAccess
            m = XposedBridge.class.getDeclaredMethod("deoptimizeMethod", Member.class);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
        deoptimizeMethod = m;
    }
    
    static void deoptimizeMethod(Class<?> c, String n) throws InvocationTargetException, IllegalAccessException {
        for (Method m : c.getDeclaredMethods()) {
            if (deoptimizeMethod != null && m.getName().equals(n)) {
                deoptimizeMethod.invoke(null, m);
                Log.d("DisableFlagSecure", "deoptimized " + m);
            }
        }
    }
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        boolean isEnable = prefs.getBoolean("disable_flag_secure", false);
        if (!isEnable) return;
        var classloader = loadPackageParam.classLoader;
        if (loadPackageParam.packageName.equals("android")) {
            try {
                deoptimizeSystemServer(classloader);
            } catch (Throwable t) {
                XposedBridge.log("deoptimize system server failed ->" + t);
            }
            
            try {
                hookWindowState(classloader);
            } catch (Throwable t) {
                XposedBridge.log("hook WindowState failed ->" + t);
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                try {
                    hookActivityTaskManagerService(classloader);
                } catch (Throwable t) {
                    XposedBridge.log("hook ActivityTaskManagerService failed ->" + t);
                }
            }
        }
    }
    
    public void deoptimizeSystemServer(ClassLoader classLoader) throws InvocationTargetException, IllegalAccessException {
        var cls = XposedHelpers.findClass("com.android.server.wm.WindowStateAnimator", classLoader);
        deoptimizeMethod(cls, "createSurfaceLocked");
        
        cls = XposedHelpers.findClass("com.android.server.wm.WindowManagerService", classLoader);
        deoptimizeMethod(cls, "relayoutWindow");
        
        for (int i = 0; i < 20; i++) {
            try {
                var clazz = classLoader.loadClass("com.android.server.wm.RootWindowContainer$$ExternalSyntheticLambda" + i);
                if (BiConsumer.class.isAssignableFrom(clazz)) {
                    deoptimizeMethod(clazz, "accept");
                }
            } catch (ClassNotFoundException ignored) {
            }
            try {
                var clazz = classLoader.loadClass("com.android.server.wm.DisplayContent$$ExternalSyntheticLambda" + i);
                if (BiPredicate.class.isAssignableFrom(clazz)) {
                    deoptimizeMethod(clazz, "test");
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
    }
    
    private void hookWindowState(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var windowStateClazz = classLoader.loadClass("com.android.server.wm.WindowState");
        Method isSecureLockedMethod;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isSecureLockedMethod = windowStateClazz.getDeclaredMethod("isSecureLocked");
        } else {
            var windowManagerServiceClazz = classLoader.loadClass("com.android.server.wm.WindowManagerService");
            isSecureLockedMethod = windowManagerServiceClazz.getDeclaredMethod("isSecureLocked", windowStateClazz);
        }
        XposedHelpers.findAndHookMethod(windowStateClazz, isSecureLockedMethod.getName(), XC_MethodReplacement.returnConstant(false));
    }
    
    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void hookActivityTaskManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var activityTaskManagerServiceClazz = classLoader.loadClass("com.android.server.wm.ActivityTaskManagerService");
        var iBinderClazz = classLoader.loadClass("android.os.IBinder");
        var iScreenCaptureObserverClazz = classLoader.loadClass("android.app.IScreenCaptureObserver");
        var method = activityTaskManagerServiceClazz.getDeclaredMethod("registerScreenCaptureObserver", iBinderClazz, iScreenCaptureObserverClazz);
        XposedHelpers.findAndHookMethod(activityTaskManagerServiceClazz, method.getName(), XC_MethodReplacement.DO_NOTHING);
    }
}
