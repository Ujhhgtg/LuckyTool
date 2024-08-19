package com.luckyzyx.luckytool.hook.scopes.android;

import static com.luckyzyx.luckytool.utils.SPUtilsKt.ModulePrefs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceControl;

import com.luckyzyx.luckytool.BuildConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressLint("PrivateApi")
public class DisableFlagSecure implements IXposedHookLoadPackage {
    private static final String SYSTEMUI = "com.android.systemui";
    private static final String OPLUS_APPPLATFORM = "com.oplus.appplatform";
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
        if (!loadPackageParam.isFirstApplication) return;
        
        boolean isEnable = prefs.getBoolean("disable_flag_secure", false);
        if (!isEnable) return;
        
        var classloader = loadPackageParam.classLoader;
        var packName = loadPackageParam.packageName;
        switch (packName) {
            case "android": {
                try {
                    deoptimizeSystemServer(classloader);
                } catch (Throwable t) {
                    XposedBridge.log("DisableFlagSecure: deoptimize system server failed ->" + t);
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    // Screen record detection (V)
                    try {
                        hookWindowManagerService(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("hook WindowManagerService failed ->" + t);
                    }
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    // Screenshot detection (U~V)
                    try {
                        hookActivityTaskManagerService(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("DisableFlagSecure: hook ActivityTaskManagerService failed ->" + t);
                    }
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // ScreenCapture in WindowManagerService (S~V)
                    try {
                        hookScreenCapture(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("DisableFlagSecure: hook ScreenCapture failed ->" + t);
                    }
                    
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        // Blackout permission check (S~T)
                        try {
                            hookActivityManagerService(classloader);
                        } catch (Throwable t) {
                            XposedBridge.log("DisableFlagSecure: hook ActivityManagerService failed ->" + t);
                        }
                    }
                    
                    // WifiDisplay (S~V) / OverlayDisplay (S~V) / VirtualDisplay (U~V)
                    try {
                        hookDisplayControl(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("DisableFlagSecure: hook DisplayControl failed ->" + t);
                    }
                    
                    // VirtualDisplay with MediaProjection (S~V)
                    try {
                        hookVirtualDisplayAdapter(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("DisableFlagSecure: hook VirtualDisplayAdapter failed ->" + t);
                    }
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // OneUI
                    try {
                        hookScreenshotHardwareBuffer(classloader);
                    } catch (Throwable t) {
                        if (!(t instanceof ClassNotFoundException)) {
                            XposedBridge.log("DisableFlagSecure: hook ScreenshotHardwareBuffer failed ->" + t);
                        }
                    }
                    try {
                        hookOneUI(classloader);
                    } catch (Throwable t) {
                        if (!(t instanceof ClassNotFoundException)) {
                            XposedBridge.log("DisableFlagSecure: hook OneUI failed ->" + t);
                        }
                    }
                }
                
                // secureLocked flag (S-)
                try {
                    // Screenshot
                    hookWindowState(classloader);
                } catch (Throwable t) {
                    XposedBridge.log("DisableFlagSecure: hook WindowState failed ->" + t);
                }
                break;
            }
            case OPLUS_APPPLATFORM: {
                // OPlus AppPlatform 13.1.0 / 14.0.0
                try {
                    hookScreenshotHardwareBuffer(classloader);
                } catch (Throwable t) {
                    if (!(t instanceof ClassNotFoundException)) {
                        XposedBridge.log("hook ScreenshotHardwareBuffer failed ->" + t);
                    }
                }
            }
            case SYSTEMUI: {
                if (OPLUS_APPPLATFORM.equals(packName) ||
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                                Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) {
                    // ScreenCapture in App (S~T) (OPlus S-U)
                    try {
                        hookScreenCapture(classloader);
                    } catch (Throwable t) {
                        XposedBridge.log("DisableFlagSecure: hook ScreenCapture failed ->" + t);
                    }
                }
                break;
            }
        }
    }
    
    public void deoptimizeSystemServer(ClassLoader classLoader) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        var cls = classLoader.loadClass("com.android.server.wm.WindowStateAnimator");
        deoptimizeMethod(cls, "createSurfaceLocked");
        
        cls = classLoader.loadClass("com.android.server.wm.WindowManagerService");
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
    
    @SuppressLint("ObsoleteSdkInt")
    private void hookWindowState(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var windowStateClazz = classLoader.loadClass("com.android.server.wm.WindowState");
        Method isSecureLockedMethod;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isSecureLockedMethod = windowStateClazz.getDeclaredMethod("isSecureLocked");
        } else {
            var windowManagerServiceClazz = classLoader.loadClass("com.android.server.wm.WindowManagerService");
            isSecureLockedMethod = windowManagerServiceClazz.getDeclaredMethod("isSecureLocked", windowStateClazz);
        }
        XposedBridge.hookMethod(isSecureLockedMethod, XC_MethodReplacement.returnConstant(false));
    }
    
    private void hookMethods(Class<?> clazz, XC_MethodHook hook, String... names) {
        var list = Arrays.asList(names);
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> list.contains(method.getName()))
                .forEach(method -> XposedBridge.hookMethod(method, hook));
    }
    
    private static Field captureSecureLayersField;
    private static Field allowProtectedField;
    
    @TargetApi(Build.VERSION_CODES.S)
    private void hookScreenCapture(ClassLoader classLoader) throws ClassNotFoundException, NoSuchFieldException {
        var screenCaptureClazz = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ?
                classLoader.loadClass("android.window.ScreenCapture") : SurfaceControl.class;
        var captureArgsClazz = classLoader.loadClass(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ?
                "android.window.ScreenCapture$CaptureArgs" : "android.view.SurfaceControl$CaptureArgs");
        captureSecureLayersField = captureArgsClazz.getDeclaredField("mCaptureSecureLayers");
        captureSecureLayersField.setAccessible(true);
        allowProtectedField = captureArgsClazz.getDeclaredField("mAllowProtected");
        allowProtectedField.setAccessible(true);
        var beforeHooks = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                var captureArgs = param.args[0];
                try {
                    captureSecureLayersField.set(captureArgs, true);
                    allowProtectedField.set(captureArgs, true);
                } catch (IllegalAccessException t) {
                    XposedBridge.log("DisableFlagSecure: ScreenCaptureHooker failed ->" + t);
                }
            }
        };
        hookMethods(screenCaptureClazz, beforeHooks, "nativeCaptureDisplay");
        hookMethods(screenCaptureClazz, beforeHooks, "nativeCaptureLayers");
    }
    
    @TargetApi(Build.VERSION_CODES.S)
    private void hookDisplayControl(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var displayControlClazz = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ?
                classLoader.loadClass("com.android.server.display.DisplayControl") : SurfaceControl.class;
        var method = displayControlClazz.getDeclaredMethod(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM ?
                        "createVirtualDisplay" :
                        "createDisplay", String.class, boolean.class);
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    String stack = Log.getStackTraceString(new Throwable());
                    if (stack.contains("createVirtualDisplayLocked")) {
                        return;
                    }
                }
                param.args[1] = true;
            }
        });
    }
    
    @TargetApi(Build.VERSION_CODES.S)
    private void hookVirtualDisplayAdapter(ClassLoader classLoader) throws ClassNotFoundException {
        var displayControlClazz = classLoader.loadClass("com.android.server.display.VirtualDisplayAdapter");
        hookMethods(displayControlClazz, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                var caller = (int) param.args[2];
                if (caller != 1000 && param.args[1] == null) {
                    // not os and not media projection
                    return;
                }
                for (int i = 3; i < param.args.length; i++) {
                    var arg = param.args[i];
                    if (arg instanceof Integer) {
                        var flags = (int) arg;
                        flags |= DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE;
                        param.args[i] = flags;
                        return;
                    }
                }
                XposedBridge.log("DisableFlagSecure: flag not found in CreateVirtualDisplayLockedHooker");
            }
        }, "createVirtualDisplayLocked");
    }
    
    @TargetApi(Build.VERSION_CODES.S)
    private void hookScreenshotHardwareBuffer(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var screenshotHardwareBufferClazz = classLoader.loadClass(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ?
                        "android.window.ScreenCapture$ScreenshotHardwareBuffer" :
                        "android.view.SurfaceControl$ScreenshotHardwareBuffer");
        var method = screenshotHardwareBufferClazz.getDeclaredMethod("containsSecureLayers");
        XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(false));
    }
    
    @TargetApi(Build.VERSION_CODES.S)
    private void hookOneUI(ClassLoader classLoader) throws ClassNotFoundException {
        var wmScreenshotControllerClazz = classLoader.loadClass("com.android.server.wm.WmScreenshotController");
        hookMethods(wmScreenshotControllerClazz, XC_MethodReplacement.returnConstant(true), "canBeScreenshotTarget");
    }
    
    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void hookActivityTaskManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var activityTaskManagerServiceClazz = classLoader.loadClass("com.android.server.wm.ActivityTaskManagerService");
        var iBinderClazz = classLoader.loadClass("android.os.IBinder");
        var iScreenCaptureObserverClazz = classLoader.loadClass("android.app.IScreenCaptureObserver");
        var method = activityTaskManagerServiceClazz.getDeclaredMethod("registerScreenCaptureObserver", iBinderClazz, iScreenCaptureObserverClazz);
        XposedBridge.hookMethod(method, XC_MethodReplacement.DO_NOTHING);
    }
    
    @TargetApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private void hookWindowManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var windowManagerServiceClazz = classLoader.loadClass("com.android.server.wm.WindowManagerService");
        var iScreenRecordingCallbackClazz = classLoader.loadClass("android.window.IScreenRecordingCallback");
        var method = windowManagerServiceClazz.getDeclaredMethod("registerScreenRecordingCallback", iScreenRecordingCallbackClazz);
        XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(false));
    }
    
    @TargetApi(Build.VERSION_CODES.S)
    private void hookActivityManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var activityTaskManagerServiceClazz = classLoader.loadClass("com.android.server.am.ActivityManagerService");
        var method = activityTaskManagerServiceClazz.getDeclaredMethod("checkPermission", String.class, int.class, int.class);
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                var permission = param.args[0];
                if ("android.permission.CAPTURE_BLACKOUT_CONTENT".equals(permission)) {
                    param.args[0] = "android.permission.READ_FRAME_BUFFER";
                }
            }
        });
    }
}
